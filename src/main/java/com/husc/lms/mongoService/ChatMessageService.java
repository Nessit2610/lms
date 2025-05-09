package com.husc.lms.mongoService;

import com.husc.lms.mongoEntity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageService {
    ChatMessage sendMessage(String chatBoxId, String senderAccount, String content);

    Page<ChatMessage> getMessagesByChatBoxId(String chatBoxId, Pageable pageable);
}
