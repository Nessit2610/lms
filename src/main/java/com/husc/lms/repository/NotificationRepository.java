package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.entity.Account;


public interface NotificationRepository extends JpaRepository<Notification, String>{
//	List<Notification> findByCreateAccount(Account createAccount);
	List<Notification> findByReceiveAccountAndType(Account account, NotificationType type);

}
