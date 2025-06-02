package com.javafood.server.mapper;

import com.javafood.server.dto.request.PaymentRequest;
import com.javafood.server.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymenthMapper {
    PaymentEntity toPaymentEntity(PaymentRequest paymentRequest);

}
