package com.javafood.server.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import com.javafood.server.dto.request.ProductRequest;
import com.javafood.server.dto.response.ProductResponse;
import com.javafood.server.entity.CategoryEntity;
import com.javafood.server.entity.DiscountEntity;
import com.javafood.server.entity.ImageEntity;
import com.javafood.server.entity.ProductEntity;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.ProductMapper;
import com.javafood.server.repository.CategoryRepository;
import com.javafood.server.repository.DiscountRepository;
import com.javafood.server.repository.ImageRepository;
import com.javafood.server.repository.ProductReposity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductService {
    static final String adminRole = "hasAuthority('SCOPE_ADMIN')";
    static final String userRole = "hasAuthority('SCOPE_USER')";
    @Value("${file.upload-dir:uploads}")
    String uploadDir;
    @Autowired
    ImageRepository imageRepo;

    @Autowired
    ProductReposity productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    DiscountRepository discountRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    ImageUploadService imageUploadService;

    @Autowired
    Cloudinary cloudinary;

    @PreAuthorize(adminRole)
    public List<ProductResponse> getAllProducts() {
        List<ProductEntity> list = productRepository.findAllWithCategory();
        return list.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USER')")
    public ProductResponse getProductsDetail(Integer productId) {
        log.info("getProductsDetail " + productId);
        try {
            ProductEntity productEntity = productRepository.findProductById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));
            return productMapper.toProductResponse(productEntity);
        } catch(Exception e){
            throw e;
        }
    }

    @PreAuthorize(adminRole)
    public Page<ProductResponse> getProductPagination(int pageNo, int pageSize, String sortBy, String sortDir) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
            Page<ProductEntity> productsPagination = productRepository.getProductsWithPagination(pageable);
            return productsPagination.map(productMapper::toProductResponse);
        } catch(Exception e){
            log.error("Lỗi khi lấy danh sách sản phẩm phân trang: ", e);
            throw e;
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USER')")
    public Page<ProductResponse> getProductToClient(int pageNo, int pageSize, String sortBy, String sortDir) {
        try {
            // Tạo Sort object
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            // Tạo Pageable
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

            // Lấy dữ liệu từ repository
            Page<ProductEntity> productsPagination = productRepository.getProductsToClient(pageable);

            // Map sang ProductResponse
            return productsPagination.map(productMapper::toProductResponse);

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách sản phẩm phân trang: ", e);
            throw new RuntimeException("Không thể lấy danh sách sản phẩm", e);
        }
    }

    @PreAuthorize(adminRole)
    public ProductResponse createProduct(ProductRequest productRequest) throws IOException {
        if(productRepository.existsByProductName(productRequest.getProductName())) {
            throw new AppException(ErrorCode.EXISTS_DATA);
        }

        CategoryEntity category = categoryRepo.findByCategoryId(productRequest.getCategoryId());

        ProductEntity productEntity = ProductEntity.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .unit(productRequest.getUnit())
                .origin(productRequest.getOrigin())
                .tags(productRequest.getTags())
                .isActive(productRequest.getIsActive())
                .price(productRequest.getPrice())
                .category(category)
                .build();

        List<ImageEntity> listImages = new ArrayList<>();
        if (productRequest.getImage() != null) {
            for (MultipartFile image : productRequest.getImage()) {
                if (image.getSize() > 10 * 1024 * 1024) { // 10MB limit
                    throw new AppException(ErrorCode.FILE_TOO_LARGE);
                }

                String imageUrl = imageUploadService.uploadImage(image);


                ImageEntity imageEntity = ImageEntity.builder()
                        .url(imageUrl)
                        .product(productEntity)
                        .build();

                listImages.add(imageEntity);
            }
        }

        productEntity.setImages(listImages);

        productRepository.save(productEntity);
        imageRepo.saveAll(listImages);

        return productMapper.toProductResponse(productEntity);
    }

    @PreAuthorize(adminRole)
    public ProductResponse updateProduct(Integer productId, ProductRequest productRequest) throws IOException {
        ProductEntity productEntity = productRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));

        // Backup ảnh cũ để rollback nếu cần
        List<ImageEntity> oldImages = new ArrayList<>(productEntity.getImages());

        List<ImageEntity> newImages = new ArrayList<>();
        List<String> newImageUrls = new ArrayList<>(); // phục vụ rollback nếu cần

        // ✅ Bước 1: Upload ảnh mới lên Cloudinary
        if (productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
            try {
                for (MultipartFile image : productRequest.getImage()) {
                    if (image.getSize() > 10 * 1024 * 1024) {
                        throw new AppException(ErrorCode.FILE_TOO_LARGE);
                    }

                    // Upload lên Cloudinary
                    String imageUrl = imageUploadService.uploadImage(image);
                    newImageUrls.add(imageUrl);

                    ImageEntity imageEntity = ImageEntity.builder()
                            .url(imageUrl)
                            .product(productEntity)
                            .build();
                    newImages.add(imageEntity);
                }

                // Lưu ảnh mới vào DB
                imageRepo.saveAll(newImages);

            } catch (Exception e) {
                // Nếu thất bại, rollback ảnh đã upload
                for (String url : newImageUrls) {
                    try {
                        String publicId = extractPublicId(url);
                        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
                    } catch (Exception ex) {
                        log.error("Không thể rollback ảnh Cloudinary: " + url, ex);
                    }
                }
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
        }

        try {
            // ✅ Bước 2: Cập nhật thông tin sản phẩm chỉ khi trường không null
            if (productRequest.getProductName() != null) {
                productEntity.setProductName(productRequest.getProductName());
            }
            if (productRequest.getDescription() != null) {
                productEntity.setDescription(productRequest.getDescription());
            }
            if (productRequest.getUnit() != null) {
                productEntity.setUnit(productRequest.getUnit());
            }
            if (productRequest.getOrigin() != null) {
                productEntity.setOrigin(productRequest.getOrigin());
            }
            if (productRequest.getTags() != null) {
                productEntity.setTags(productRequest.getTags());
            }
            if (productRequest.getPrice() != null) {
                productEntity.setPrice(productRequest.getPrice());
            }
            if (productRequest.getCategoryId() != null) {
                CategoryEntity category = categoryRepo.findByCategoryId(productRequest.getCategoryId());
                if (category == null) {
                    throw new AppException(ErrorCode.NOT_EXISTS_DATA);
                }
                productEntity.setCategory(category);
            }

            if (productRequest.getDiscountId() != null) {
                DiscountEntity discount = discountRepo.findByDiscountId(productRequest.getDiscountId());
                if (discount == null) {
                    throw new AppException(ErrorCode.NOT_EXISTS_DATA);
                }
                productEntity.setDiscount(discount);
            } else {
                productEntity.setDiscount(null);
            }

            productEntity.setUpdatedAt(LocalDateTime.now());

            // ✅ Bước 3: Nếu có ảnh mới → Xóa bản ghi ảnh cũ & gán ảnh mới
            if (!newImages.isEmpty()) {
                for (ImageEntity oldImage : oldImages) {
                    productEntity.getImages().remove(oldImage);
                    imageRepo.delete(oldImage);
                }

                productEntity.getImages().addAll(newImages);
            }

            // ✅ Bước 4: Lưu cập nhật
            ProductEntity savedProduct = productRepository.save(productEntity);
            return productMapper.toProductResponse(savedProduct);

        } catch (Exception e) {
            // ✅ Bước 5: Rollback ảnh mới đã upload
            for (String url : newImageUrls) {
                try {
                    String publicId = extractPublicId(url);
                    cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
                } catch (Exception ex) {
                    log.error("Không thể rollback ảnh Cloudinary: " + url, ex);
                }
            }

            // Xóa bản ghi ảnh mới trong DB nếu có
            if (!newImages.isEmpty()) {
                try {
                    imageRepo.deleteAll(newImages);
                } catch (Exception ex) {
                    log.error("Rollback DB ảnh thất bại", ex);
                }
            }

            throw new AppException(ErrorCode.INVALID_INPUT);
        }
    }

    @PreAuthorize(adminRole)
    public void deleteProduct(Integer productId) {
        boolean isExist = productRepository.existsByProductId(productId);
        if(!isExist) throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        productRepository.deleteById(productId);
    }

    @PreAuthorize(adminRole)
    public void activeProduct(Integer productId, ProductRequest productRequest) {
        ProductEntity productEntity = productRepository.findProductById(productId).orElseThrow(()-> new AppException(ErrorCode.NOT_EXISTS_DATA));
        productEntity.setIsActive(productRequest.getIsActive());
        productRepository.save(productEntity);
    }

    private String extractPublicId(String secureUrl) {
        String[] parts = secureUrl.split("/");
        int index = Arrays.asList(parts).indexOf("upload");
        StringBuilder publicId = new StringBuilder();
        for (int i = index + 1; i < parts.length; i++) {
            publicId.append("/").append(parts[i]);
        }
        // Xóa đuôi .jpg/.png/...
        String fullId = publicId.toString().replaceFirst("\\.[^.]+$", "");
        return fullId.startsWith("/") ? fullId.substring(1) : fullId;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USER')")
    public Page<ProductResponse> getProductByCategory(int pageNo, int pageSize, String sortBy, String sortDir, Integer categoryId) {
        try {

            // Tạo Sort object
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            // Tạo Pageable
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

            // Lấy dữ liệu từ repository
            Page<ProductEntity> productsPagination = productRepository.getProductsByCategory(pageable, categoryId);

            // Map sang ProductResponse
            return productsPagination.map(productMapper::toProductResponse);

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách sản phẩm phân trang: ", e);
            throw new RuntimeException("Không thể lấy danh sách sản phẩm", e);
        }
    }

}
