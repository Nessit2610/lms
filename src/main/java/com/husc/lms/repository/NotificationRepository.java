package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

}
