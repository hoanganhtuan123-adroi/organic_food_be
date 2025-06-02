package com.javafood.server.service;

import com.javafood.server.dto.request.OrderDetailRequest;
import com.javafood.server.dto.request.OrderRequest;
import com.javafood.server.dto.request.PaymentRequest;
import com.javafood.server.dto.response.BestSellingProduct;
import com.javafood.server.dto.response.OrderResponse;
import com.javafood.server.dto.response.RevenueReport;
import com.javafood.server.dto.response.SimpleOrderResponse;
import com.javafood.server.entity.*;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.OrderMapper;
import com.javafood.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    static final String adminRole = "hasAuthority('SCOPE_ADMIN')";
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductReposity productRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    TemplateEngine templateEngine;

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USER')")
    public OrderResponse createOrder(OrderRequest orderRequest) {
        if (orderRequest.getUserId() == null || orderRequest.getSubtotalAmount() == null ||
                orderRequest.getShippingAddress() == null || orderRequest.getShippingMethod() == null ||
                orderRequest.getFinalAmount() == null || orderRequest.getOrderDetails() == null ||
                orderRequest.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không hợp lệ");
        }

        UserEntity user = userRepository.findById(orderRequest.getUserId()).orElseThrow( ()-> new AppException(ErrorCode.NOT_EXISTS_DATA));
        OrderEntity orderEntity = OrderEntity.builder()
                .user(user)
                .orderDate(orderRequest.getOrderDate() != null ? orderRequest.getOrderDate() : LocalDateTime.now())
                .subtotalAmount(orderRequest.getSubtotalAmount())
                .shippingAddress(orderRequest.getShippingAddress())
                .shippingMethod(orderRequest.getShippingMethod())
                .shippingFee(orderRequest.getShippingFee() != null ? orderRequest.getShippingFee() : BigDecimal.valueOf(0.0))
                .finalAmount(orderRequest.getFinalAmount())
                .status(orderRequest.getStatus())
                .build();

        orderEntity = orderRepository.save(orderEntity);

        // Tạo và lưu OrderDetailEntity
        Set<OrderDetailEntity> orderDetails = new HashSet<>();
        for (OrderDetailRequest detailRequest : orderRequest.getOrderDetails()) {
            // Kiểm tra productId (nếu không null)
            ProductEntity product = null;
            if (detailRequest.getProductId() != null) {
                product = productRepository.findById(detailRequest.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại: " + detailRequest.getProductId()));
            }

            OrderDetailEntity detail = new OrderDetailEntity();
            detail.setOrder(orderEntity);
            detail.setProduct(product);
            detail.setQuantity(detailRequest.getQuantity());
            detail.setOriginalPrice(detailRequest.getOriginalPrice());
            detail.setFinalPrice(detailRequest.getFinalPrice());
            orderDetails.add(detail);
        }
        orderDetailRepository.saveAll(orderDetails);


        // Tạo và lưu PaymentEntity (nếu có)
        Set<PaymentEntity> payments = new HashSet<>();
        if (orderRequest.getPayments() != null && !orderRequest.getPayments().isEmpty()) {
            for (PaymentRequest paymentRequest : orderRequest.getPayments()) {
                if (paymentRequest.getAmount() == null || paymentRequest.getPaymentMethod() == null) {
                    throw new IllegalArgumentException("Dữ liệu thanh toán không hợp lệ");
                }

                PaymentEntity payment = new PaymentEntity();
                payment.setOrder(orderEntity);
                payment.setPaymentMethod(paymentRequest.getPaymentMethod());
                payment.setPaymentDate(paymentRequest.getPaymentDate() != null ? paymentRequest.getPaymentDate() : LocalDateTime.now());
                payment.setTransactionId(paymentRequest.getTransactionId());
                payment.setAmount(paymentRequest.getAmount());
                payment.setTransactionStatus(paymentRequest.getTransactionStatus());
                payments.add(payment);
            }
            paymentRepository.saveAll(payments);
        }

        try {
            String customerEmail = user.getEmail();
            Context context = new Context();
            context.setVariable("customerName", user.getUsername());
            context.setVariable("orderId", orderEntity.getOrderId());
            context.setVariable("orderDate", orderEntity.getOrderDate().toString());
            context.setVariable("finalAmount", orderEntity.getFinalAmount().toString());

            String emailContent = templateEngine.process("order-confirmation", context);

            Email email = new Email();
            email.setEmail(customerEmail);
            email.setSubject("Xác nhận đơn hàng #" + orderEntity.getOrderId());
            email.setBody(emailContent);

            emailService.sendEmail(email);

        } catch(Exception e){
            // Log lỗi nhưng không làm ảnh hưởng đến quá trình tạo đơn hàng
            System.err.println("Lỗi khi gửi email xác nhận đơn hàng: " + e.getMessage());
        }

        OrderResponse orderResponse = orderMapper.toOrderResponse(orderEntity);
        orderResponse.setOrderDetails(orderMapper.toOrderDetailResponseList(new ArrayList<>(orderDetails)));
        orderResponse.setPayments(orderMapper.toPaymentResponseList(new ArrayList<>(payments)));
        return orderResponse;
    }

    // API 1: Lấy danh sách đơn hàng với phân trang
    @Transactional(readOnly = true)
    public Page<SimpleOrderResponse> getAllOrders(Pageable pageable) {
        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(orderMapper::toSimpleOrderResponse);
    }

    // API 2: Lấy chi tiết một đơn hàng
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public OrderResponse getOrderById(Integer orderId) {
        OrderEntity order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại: " + orderId));

        OrderResponse response = orderMapper.toOrderResponse(order);
        response.setOrderDetails(orderMapper.toOrderDetailResponseList(new ArrayList<>(order.getOrderDetails())));
        response.setPayments(orderMapper.toPaymentResponseList(new ArrayList<>(order.getPayments())));
        return response;
    }

    // API 3: Lấy danh sách đơn hàng theo ID người dùng
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_USER')")
    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        List<OrderEntity> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng cho người dùng này");
        }
        return orders.stream().map(order -> {
            OrderResponse response = orderMapper.toOrderResponse(order);
            response.setOrderDetails(orderMapper.toOrderDetailResponseList(new ArrayList<>(order.getOrderDetails())));
            response.setPayments(orderMapper.toPaymentResponseList(new ArrayList<>(order.getPayments())));
            return response;
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public RevenueReport getRevenueReport(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate phải trước endDate");
        }
        BigDecimal totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        Long numberOfOrders = orderRepository.getNumberOfOrders(startDate, endDate);
        BigDecimal averageOrderValue = numberOfOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(numberOfOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        return new RevenueReport(totalRevenue, numberOfOrders, averageOrderValue);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<BestSellingProduct> getBestSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate phải trước endDate");
        }
        List<Object[]> results = orderDetailRepository.findBestSellingProducts(startDate, endDate);
        return results.stream()
                .limit(limit)
                .map(obj -> {
                    ProductEntity product = (ProductEntity) obj[0];
                    Long totalQuantity = (Long) obj[1];
                    BestSellingProduct bsp = new BestSellingProduct();
                    bsp.setProductId(product.getProductId());
                    bsp.setProductName(product.getProductName());
                    bsp.setTotalQuantitySold(totalQuantity);
                    return bsp;
                })
                .collect(Collectors.toList());
    }


}
