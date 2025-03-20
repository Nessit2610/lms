package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.InvalidatedToken;



@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

}
