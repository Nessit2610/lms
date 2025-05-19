package com.husc.lms.dto.response;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.PaymentEntity;
import com.husc.lms.entity.Student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntityResponse {

    private String paymentId;

    private CourseViewResponse course;

    private StudentViewResponse student;
    
    private String status;
    private String email;
    private String countryCode;
    private String postalCode;
    private float totalPrice;
    private float transactionFee;
    private String description;
    private String createTime;
}
