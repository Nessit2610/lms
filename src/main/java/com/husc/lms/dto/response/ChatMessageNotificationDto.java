package com.husc.lms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ChatMessageNotificationDto {
    private String notificationId;
    private String detail; // Content of the notification message
    private Date createdAt;
    private boolean isRead;
    private String chatMessageId; // ID of the actual ChatMessage from Mongo
    private String chatBoxId; // ID of the ChatBox this message belongs to
    // Optionally, add sender info if needed for display in notification list
    // private String senderUsername;
    // private String senderAvatar;
}