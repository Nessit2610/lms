package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

}
