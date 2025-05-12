package com.husc.lms.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

	private String notificationId;
	private String receivedAccountId;
	private String commentId;
	private String commentReplyId;
	private String notificationType;
	private Boolean isRead;
	private String description;
	private OffsetDateTime createdAt;
}
