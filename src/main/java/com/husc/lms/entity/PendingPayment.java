package com.husc.lms.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "lms_pending_payment")
public class PendingPayment {

	@Id
    private String paymentId;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Course course;

    private boolean completed;
}
