package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.JoinClassRequest;

@Repository
public interface JoinClassRequestRepository extends JpaRepository<JoinClassRequest, String> {

}
