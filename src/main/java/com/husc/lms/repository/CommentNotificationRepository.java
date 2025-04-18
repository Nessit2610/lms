package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.lms.entity.CommentNotification;

public interface CommentNotificationRepository extends JpaRepository<CommentNotification, String> {

}
