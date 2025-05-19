package com.husc.lms.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lms_payment")
public class PaymentEntity {

	@Id
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;
    
    private String status;
    private String email;
    private String countryCode;
    private String postalCode;
    private float totalPrice;
    private float transactionFee;
    private String description;
    private String createTime;
}


