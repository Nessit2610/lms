package com.husc.lms.mongoService;

import java.util.List;

import com.husc.lms.mongoEntity.ChatMessageStatus;

public interface ChatMessageStatusService {
    List<ChatMessageStatus> getMessageStatuses(String messageId);

    void markMessagesAsRead(String chatBoxId, String accountUsername);
    // Potentially other methods related to status management can be added here
    // later
}
