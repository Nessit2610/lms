package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

	private Integer countUnreadNotification;
	private List<NotificationDetail> notificationDetails;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class NotificationDetail {
		private String notificationId;
		private String receivedAccountId;
		private String courseId;
		private String lessonId;
		private String chapterId;
		private String groupId;
		private String postId;
		private String chatBoxId;
		private String notificationType;
		private Boolean isRead;
		private String description;
		private OffsetDateTime createdAt;
	}

}
