package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.CommentReadStatus;

@Repository
public interface CommentReadStatusRepository extends JpaRepository<CommentReadStatus, String>{

}
