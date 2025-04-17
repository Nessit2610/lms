package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Document;
import com.husc.lms.entity.Account;


@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
	List<Document> findAllByStatus(String status);
	Document findByIdAndAccount(String id, Account account);
	
	List<Document> findByAccount(Account account);
}
