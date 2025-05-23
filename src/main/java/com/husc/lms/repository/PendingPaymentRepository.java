package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.PendingPayment;

@Repository
public interface PendingPaymentRepository extends JpaRepository<PendingPayment, String> {

}
