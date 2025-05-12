package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.mongoEntity.ChatMessage;

import jakarta.transaction.Transactional;

import com.husc.lms.entity.Account;

public interface NotificationRepository extends JpaRepository<Notification, String> {

	List<Notification> findByAccountAndType(Account account, NotificationType type);

	@Query("SELECT n FROM Notification n WHERE n.account = :account ORDER BY n.createdAt DESC")
	Page<Notification> findByAccount(Account account, Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.isRead = true WHERE n IN :notifications")
	void setNotificationAsReadByAccount(List<Notification> notifications);
}
