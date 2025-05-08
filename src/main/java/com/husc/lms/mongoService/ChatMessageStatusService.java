package com.husc.lms.mongoService;

public interface ChatMessageStatusService {
    void markAllUnreadMessagesAsRead(String chatBoxId);
}
