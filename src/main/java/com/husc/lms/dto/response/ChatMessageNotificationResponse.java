package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageNotificationResponse {
    private String notificationId;
    private String detail; // Content of the notification message
    private OffsetDateTime createdAt;
    private boolean isRead;
    private String chatMessageId; // ID of the actual ChatMessage from Mongo
    private String chatBoxId; // ID of the ChatBox this message belongs to
    // Optionally, add sender info if needed for display in notification list
    // private String senderUsername;
    // private String senderAvatar;
}