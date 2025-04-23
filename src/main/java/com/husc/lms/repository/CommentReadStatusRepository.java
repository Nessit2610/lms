package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReadStatus;

import jakarta.transaction.Transactional;

@Repository
public interface CommentReadStatusRepository extends JpaRepository<CommentReadStatus, String>{
	
	@Modifying
	@Transactional
	@Query("UPDATE CommentReadStatus crs SET crs.isRead = true WHERE crs.comment IN :comments AND crs.account = :account")
	void setCommentsAsReadByAccount(List<Comment> comments, Account account);
}
