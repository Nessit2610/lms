package com.husc.lms.mongoService;

import com.husc.lms.mongoEntity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChatMessageService {
    ChatMessage sendMessage(String chatBoxId, String senderAccount, String content, MultipartFile file,
            String fileType );

    Page<ChatMessage> getMessagesByChatBoxId(String chatBoxId, Pageable pageable);
}
