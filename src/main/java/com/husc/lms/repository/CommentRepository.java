package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

}
