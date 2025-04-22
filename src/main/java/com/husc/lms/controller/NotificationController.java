package com.husc.lms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.CommentNotificationResponse;
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
}
