package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoEntity.ChatMessageStatus;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

	private final ChatBoxRepository chatBoxRepo;
    private final ChatBoxMemberRepository memberRepo;
    private final ChatMessageRepository messageRepo;
    private final AccountRepository accountRepo;
	
	@Override
	public ChatMessage sendMessage(String chatBoxId, String senderId, String content) {
		// Create and save message
        ChatMessage message = ChatMessage.builder()
                .chatBoxId(chatBoxId)
                .senderId(senderId)
                .content(content)
                .createdAt(new Date())
                .build();
        message = messageRepo.save(message);

        // Get all members except sender
        List<ChatBoxMember> members = memberRepo.findByChatBoxId(chatBoxId);
        List<ChatMessageStatus> statuses = new ArrayList<>();

        // Create message status for each member
        for (ChatBoxMember member : members) {
            ChatMessageStatus status = ChatMessageStatus.builder()
                    .messageId(message.getId())
                    .accountId(member.getAccountId())
                    .isRead(member.getAccountId().equals(senderId)) // true for sender, false for others
                    .readAt(member.getAccountId().equals(senderId) ? new Date() : null)
                    .build();
            statuses.add(status);
        }

        return message;
	}

}
