package com.husc.lms.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatBox")
@RequiredArgsConstructor
public class ChatHistoryController {
	private final ChatMessageRepository messageRepo;
    private final ChatService chatService;

    // API load lịch sử chat dựa trên 2 tài khoản
    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2) {

        // Lấy ChatBox giữa 2 tài khoản
        ChatBox chatBox = chatService.getOrCreatePrivateChatBox(user1, user2);
        // Lấy tin nhắn của ChatBox
        List<ChatMessage> messages = messageRepo.findByChatBoxId(chatBox.getId());
        // Sắp xếp theo thời gian tăng dần
        messages.sort(Comparator.comparing(ChatMessage::getCreatedAt));
        return ResponseEntity.ok(messages);
    }

}
