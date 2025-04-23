package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;

import jakarta.transaction.Transactional;

import com.husc.lms.entity.Account;


public interface NotificationRepository extends JpaRepository<Notification, String>{
//	List<Notification> findByCreateAccount(Account createAccount);
	List<Notification> findByReceiveAccountAndType(Account account, NotificationType type);
	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.isRead = true WHERE n IN :notifications AND n.receiveAccount = :account")
	void setNotificationAsReadByAccount(List<Notification> notifications, Account account);
}
