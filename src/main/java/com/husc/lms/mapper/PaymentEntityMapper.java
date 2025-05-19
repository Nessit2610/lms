package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.PaymentEntityResponse;
import com.husc.lms.entity.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

	PaymentEntityResponse toPaymentEntityResponse(PaymentEntity paymentEntity);
}
