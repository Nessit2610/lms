package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.PaymentEntity;

@Repository
public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, String> {

}
