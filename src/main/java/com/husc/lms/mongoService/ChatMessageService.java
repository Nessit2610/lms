package com.husc.lms.mongoService;

import com.husc.lms.mongoEntity.ChatMessage;

public interface ChatMessageService {
    ChatMessage sendMessage(String chatBoxId, String senderId, String content);
}
