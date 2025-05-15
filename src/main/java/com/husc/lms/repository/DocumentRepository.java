package com.husc.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Document;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Major;



@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
	Page<Document> findAllByStatus(String status, Pageable pageable);
	
	Document findByIdAndAccount(String id, Account account);
	
	Page<Document> findByAccount(Account account, Pageable pageable);
	
	Page<Document> findByAccountAndTitleContainingIgnoreCase(Account account, String keyword, Pageable pageable);
	
	Page<Document> findByTitleContainingIgnoreCaseAndStatusAndMajor(String title, String status, Major major, Pageable pageable);

	Page<Document> findByStatusAndMajor(String status, Major major, Pageable pageable);

}
