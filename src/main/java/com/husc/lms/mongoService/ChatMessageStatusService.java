package com.husc.lms.mongoService;

public interface ChatMessageStatusService {
    void markMessagesAsRead(String chatBoxId, String username);
    // Potentially other methods related to status management can be added here
    // later
}
