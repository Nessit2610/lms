package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;

import jakarta.transaction.Transactional;

import com.husc.lms.entity.Account;

public interface NotificationRepository extends JpaRepository<Notification, String> {

	List<Notification> findByAccountAndType(Account account, NotificationType type);

	@Query("SELECT n FROM Notification n WHERE n.account = :account ORDER BY n.createdAt DESC")
	Page<Notification> findByAccount(Account account, Pageable pageable);

	Integer countByAccountAndIsReadFalse(Account account);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :notificationIds")
	void setNotificationAsReadByAccountWithListNotificationId(List<String> notificationIds);

	@Modifying
	@Transactional
	@Query("UPDATE Notification n SET n.isRead = true WHERE n.account = :account")
	void setNotificationAsReadByAccount(@Param("account") Account account);
}
