package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Document;
import com.husc.lms.entity.Account;


@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
	Page<Document> findAllByStatus(String status, Pageable pageable);
	
	Document findByIdAndAccount(String id, Account account);
	
	Page<Document> findByAccount(Account account, Pageable pageable);
	
	@Query("SELECT d FROM Document d " +
	       "WHERE d.status = :status AND " +
	       "(LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	       "OR LOWER(d.major.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<Document> searchByStatusAndTitleOrMajor(@Param("status") String status,
	                                              @Param("keyword") String keyword,
	                                              Pageable pageable);


}
