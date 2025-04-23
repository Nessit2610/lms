package com.husc.lms.dto.request;

import java.util.Date;
import com.husc.lms.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {
	private String id;
    private NotificationType type;
    private boolean isRead;
    private String description;
    private Date createdAt;
    private String accountName;
    private String commentContent;
    private String messageContent;

}
