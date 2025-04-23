package com.husc.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.entity.Notification;
import com.husc.lms.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;
	
	@GetMapping("/comments/unread")
	public CommentNotificationResponse getUnreadCommentNotifications() {
		return notificationService.getAllUnreadCommentNotificationOfAccount();
	}
	
//    @PostMapping("/comments/read")
//    public void setNotificationAsReadByAccount(@RequestBody List<Notification> notifications) {
//        notificationService.setNotificationAsReadByAccount(notifications);
//    }
	@PostMapping("/comments/read")
	public void setNotificationAsReadByAccount(@RequestBody List<NotificationRequest> notificationRequests) {
	    // Chuyển đổi từ NotificationRequest sang Notification
	    List<Notification> notifications = notificationRequests.stream()
	        .map(this::convertToNotification) // Sử dụng phương thức chuyển đổi
	        .collect(Collectors.toList());

	    notificationService.setNotificationAsReadByAccount(notifications);
	}

	private Notification convertToNotification(NotificationRequest request) {
	    return Notification.builder()
	        .id(request.getId())
	        .type(request.getType())
	        .isRead(request.isRead())
	        .description(request.getDescription())
	        .createdAt(request.getCreatedAt()) 
	        .build();
	}
}
