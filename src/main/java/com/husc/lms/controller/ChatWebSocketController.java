package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.ChatMessageRequest;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoService.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
	private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest messageRequest) {
        String senderId = messageRequest.getSenderId();
        String receiverId = messageRequest.getReceiverId();
        String content = messageRequest.getContent();

        // Tìm (hoặc tạo) ChatBox cho 2 người
        ChatBox chatBox = chatService.getOrCreatePrivateChatBox(senderId, receiverId);
        // Lưu tin nhắn
        ChatMessage saved = chatService.saveMessage(chatBox.getId(), senderId, content);

        // Gửi tin nhắn tới người nhận qua WebSocket
        messagingTemplate.convertAndSendToUser(
                receiverId,
                "/queue/messages",
                saved
        );
        // Gửi lại cho sender (để đồng bộ UI nếu cần)
        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/messages",
                saved
        );
    }

}
