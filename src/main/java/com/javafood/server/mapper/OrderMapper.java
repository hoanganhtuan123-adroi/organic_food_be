package com.javafood.server.mapper;

import com.javafood.server.dto.response.OrderDetailResponse;
import com.javafood.server.dto.response.OrderResponse;
import com.javafood.server.dto.response.PaymentResponse;
import com.javafood.server.dto.response.SimpleOrderResponse;
import com.javafood.server.entity.OrderDetailEntity;
import com.javafood.server.entity.OrderEntity;
import com.javafood.server.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "status", target = "status", defaultValue = "PROCESSING")
    OrderResponse toOrderResponse(OrderEntity orderEntity);

    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    OrderDetailResponse toOrderDetailResponse(OrderDetailEntity orderDetailEntity);

    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "transactionStatus", target = "transactionStatus")
    PaymentResponse toPaymentResponse(PaymentEntity paymentEntity);

    List<OrderDetailResponse> toOrderDetailResponseList(List<OrderDetailEntity> orderDetails);

    List<PaymentResponse> toPaymentResponseList(List<PaymentEntity> payments);

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "payments", target = "paymentMethod", qualifiedByName = "mapPaymentMethod")
    SimpleOrderResponse toSimpleOrderResponse(OrderEntity order);

    List<SimpleOrderResponse> toSimpleOrderResponseList(List<OrderEntity> orders);

    @org.mapstruct.Named("mapPaymentMethod")
    default String mapPaymentMethod(Set<PaymentEntity> payments) {
        return payments != null && !payments.isEmpty() ? payments.iterator().next().getPaymentMethod() : "Chưa thanh toán";
    }
}
