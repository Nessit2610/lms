package com.husc.lms.dto.response;

import java.util.List;

import com.husc.lms.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentNotificationResponse {
	private List<Notification> notifications;
    private int totalCount;
    private int unreadCount;
}
