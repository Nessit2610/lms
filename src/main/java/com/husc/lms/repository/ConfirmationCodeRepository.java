package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.ConfirmationCode;


@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, String> {

	ConfirmationCode findByEmail(String email);
	
	boolean existsByEmail(String email);
	void deleteByEmail(String email);

}
