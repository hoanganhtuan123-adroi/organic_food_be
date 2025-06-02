package com.javafood.server.service;

import com.javafood.server.dto.request.CartRequest;
import com.javafood.server.dto.response.CartResponse;
import com.javafood.server.entity.CartEntity;
import com.javafood.server.entity.ProductEntity;
import com.javafood.server.entity.StockEntity;
import com.javafood.server.entity.UserEntity;
import com.javafood.server.mapper.CartMapper;
import com.javafood.server.mapper.ProductMapper;
import com.javafood.server.mapper.UserMapper;
import com.javafood.server.repository.CartRepository;
import com.javafood.server.repository.ProductReposity;
import com.javafood.server.repository.StockRepository;
import com.javafood.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service
        ;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductReposity productReposity;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockRepository stockRepository;

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void addToCart(CartRequest cartRequest) {
        // Tìm user và product
        UserEntity userEntity = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + cartRequest.getUserId()));
        ProductEntity productEntity = productReposity.findProductById(cartRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + cartRequest.getProductId()));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Optional<CartEntity> existingCart = cartRepository.findByUserAndProduct(userEntity, productEntity);
        if (existingCart.isPresent()) {
            // Nếu đã có, cập nhật số lượng
            CartEntity cartEntity = existingCart.get();
            cartEntity.setQuantity(cartEntity.getQuantity() + cartRequest.getQuantity());
            try {
                cartRepository.save(cartEntity);
                log.info("Cập nhật số lượng sản phẩm trong giỏ hàng thành công, cartId: " + cartEntity.getCartId());
            } catch (DataAccessException e) {
                log.error("Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
                throw new RuntimeException("Không thể cập nhật giỏ hàng: " + e.getMessage());
            }
        } else {
            // Nếu chưa có, tạo mới
            CartEntity cartEntity = new CartEntity();
            cartEntity.setUser(userEntity);
            cartEntity.setProduct(productEntity);
            cartEntity.setQuantity(cartRequest.getQuantity());

            try {
                CartEntity savedEntity = cartRepository.save(cartEntity);
                if (savedEntity.getCartId() != null) {
                    log.info("Thêm sản phẩm vào giỏ hàng thành công, cartId: " + savedEntity.getCartId());
                } else {
                    throw new RuntimeException("Thêm sản phẩm vào giỏ hàng thất bại: Không tạo được ID");
                }
            } catch (DataAccessException e) {
                log.error("Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
                throw new RuntimeException("Không thể thêm sản phẩm vào giỏ hàng: " + e.getMessage());
            }
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Transactional(readOnly = true)
    public List<CartResponse> getCartByUserId(Integer userId) {
        // Kiểm tra userId hợp lệ
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ: " + userId);
        }

        // Lấy danh sách giỏ hàng từ repository
        List<CartEntity> cartEntities = cartRepository.findCartByUserId(userId);
        if (cartEntities.isEmpty()) {
            log.info("Không tìm thấy giỏ hàng cho người dùng với ID: " + userId);
            return Collections.emptyList();
        }

        // Ánh xạ từ CartEntity sang CartResponse
        return cartEntities.stream()
                .map(cartMapper::toCartResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void deleteCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public void updateQuantity(CartRequest cartRequest) {
        CartEntity cartEntity = cartRepository.findById(cartRequest.getCartId()).orElseThrow(()-> new RuntimeException("Không tìm thấy giỏ hàng"));
       if(productReposity.existsByProductId(cartRequest.getProductId())){
           StockEntity stockEntity = stockRepository.findByProduct_ProductId(cartRequest.getProductId()).orElseThrow(()-> new RuntimeException("San pham het hang"));
           if(stockEntity.getQuantity() < cartRequest.getQuantity()){
               throw new RuntimeException("Không đủ hàng trong kho. Số lượng có sẵn");
           }
           cartEntity.setQuantity(cartRequest.getQuantity());
           cartRepository.save(cartEntity);
       }
    }


}
