package com.javafood.server.service;

import com.javafood.server.dto.request.ProductRequest;
import com.javafood.server.dto.response.ProductResponse;
import com.javafood.server.entity.ImageEntity;
import com.javafood.server.entity.ProductEntity;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.ProductMapper;
import com.javafood.server.repository.ImageRepository;
import com.javafood.server.repository.ProductReposity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductService {
    static final String adminRole = "hasAuthority('SCOPE_ADMIN')";
    @Autowired
    ImageRepository imageRepo;

    @Autowired
    ProductReposity productRepository;

    @Autowired
    ProductMapper productMapper;

    @PreAuthorize(adminRole)
    public List<ProductResponse> getAllProducts() {
        List<ProductEntity> list = productRepository.findAllWithCategory();
        return list.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @PreAuthorize(adminRole)
    public ProductResponse getProductsDetail(Integer productId) {
        log.info("getProductsDetail " + productId);
        try {
            ProductEntity productEntity = productRepository.findProductById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));
            return productMapper.toProductResponse(productEntity);
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }

    }

    @PreAuthorize(adminRole)
    public ProductResponse createProduct(ProductRequest productRequest) throws IOException {
        String UPLOAD_DIR = "uploads/";
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        ProductEntity productEntity = ProductEntity.builder()
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .unit(productRequest.getUnit())
                .origin(productRequest.getOrigin())
                .tags(productRequest.getTags())
                .isActive(productRequest.getIsActive())
                .build();

        List<ImageEntity> listImages = new ArrayList<>();
        if (productRequest.getImage() != null) {
            for (MultipartFile image : productRequest.getImage()) {
                String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String imageUrl = "/uploads/" + fileName;

                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setUrl(imageUrl);
                imageEntity.setProduct(productEntity);

                listImages.add(imageEntity);
            }
        }
        productEntity.setImages(listImages);

        productRepository.save(productEntity);

        imageRepo.saveAll(listImages);

        return productMapper.toProductResponse(productEntity);
    }

    /*
    @PreAuthorize(adminRole)
    public ProductResponse updateProduct(Integer productId, ProductRequest productRequest) throws IOException {

        String UPLOAD_DIR = "uploads/";
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        ProductEntity productEntity = productRepository.findByProductId(productId).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));

        List<ImageEntity> oldImages = new ArrayList<>(productEntity.getImages());
        for (ImageEntity img : oldImages) {
            String imgPath = img.getUrl().replace("/uploads/", "");
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + imgPath));
            } catch (IOException e) {
                log.error("Không thể xóa file: " + imgPath, e);
            }
            productEntity.getImages().remove(img);
        }

        productEntity.setProductName(productRequest.getProductName());
        productEntity.setDescription(productRequest.getDescription());
        productEntity.setUnit(productRequest.getUnit());
        productEntity.setOrigin(productRequest.getOrigin());
        productEntity.setTags(productRequest.getTags());
        productEntity.setIsActive(productRequest.getIsActive());
        productEntity.setPrice(productRequest.getPrice());
        productEntity.setUpdatedAt(LocalDateTime.now());

        productRepository.save(productEntity);

        List<ImageEntity> listImages = new ArrayList<>();
        if (productRequest.getImage() != null) {
            for (MultipartFile image : productRequest.getImage()) {
                String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                String imageUrl = "/uploads/" + fileName;

                ImageEntity imageEntity = ImageEntity.builder().url(imageUrl).product(productEntity).build();
                listImages.add(imageEntity);
            }
        }
        productEntity.getImages().clear();

        productEntity.getImages().addAll(listImages);

        List<ImageEntity> imageEntity = imageRepo.findByProductProductId(productId);
        for (int i = 0; i < imageEntity.size() && i < listImages.size(); i++) {
            imageEntity.get(i).setUrl(listImages.get(i).getUrl());
        }

        if(!listImages.isEmpty()) {
            imageRepo.saveAll(imageEntity);
            productRepository.save(productEntity);
        }

        return productMapper.toProductResponse(productEntity);
    }
    */


    @PreAuthorize(adminRole)
    public ProductResponse updateProduct(Integer productId, ProductRequest productRequest) throws IOException {

        String UPLOAD_DIR = "uploads/";
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tìm productEntity
        ProductEntity productEntity = productRepository.findByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));

        // Bước 1: Xóa ảnh cũ
        List<ImageEntity> oldImages = new ArrayList<>(productEntity.getImages());
        for (ImageEntity img : oldImages) {
            String imgPath = img.getUrl().replace("/uploads/", "");
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + imgPath));
            } catch (IOException e) {
                log.error("Không thể xóa file: " + imgPath, e);
            }
            productEntity.getImages().remove(img);
            imageRepo.delete(img); // Xóa bản ghi ảnh cũ khỏi database
        }

        // Cập nhật các trường khác của productEntity
        productEntity.setProductName(productRequest.getProductName());
        productEntity.setDescription(productRequest.getDescription());
        productEntity.setUnit(productRequest.getUnit());
        productEntity.setOrigin(productRequest.getOrigin());
        productEntity.setTags(productRequest.getTags());
        productEntity.setIsActive(productRequest.getIsActive());
        productEntity.setPrice(productRequest.getPrice());
        productEntity.setUpdatedAt(LocalDateTime.now());

        // Bước 2: Thêm ảnh mới
        List<ImageEntity> listImages = new ArrayList<>();
        if (productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
            for (MultipartFile image : productRequest.getImage()) {
                String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                String imageUrl = "/uploads/" + fileName;

                ImageEntity imageEntity = ImageEntity.builder()
                        .url(imageUrl)
                        .product(productEntity)
                        .build();
                listImages.add(imageEntity);
            }
            imageRepo.saveAll(listImages); // Lưu danh sách ảnh mới vào database
            productEntity.getImages().addAll(listImages); // Liên kết ảnh mới với productEntity
        }

        // Bước 3: Lưu productEntity để đồng bộ hóa
        productRepository.save(productEntity);

        return productMapper.toProductResponse(productEntity);
    }

}
