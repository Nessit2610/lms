package com.husc.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.dto.response.NotificationResponse;
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

	@PostMapping("/comments/read")
	public void setNotificationAsReadByAccount(@RequestBody List<NotificationRequest> notificationRequests) {
		notificationService.markCommentNotificationsAsRead(notificationRequests);
	}

	@GetMapping("")
	public APIResponse<Page<NotificationResponse>> getNotificationsByAccount(
			@RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize) {
		return APIResponse.<Page<NotificationResponse>>builder()
				.result(notificationService.getNotificationsByAccount(pageNumber, pageSize))
				.build();
	}
}
