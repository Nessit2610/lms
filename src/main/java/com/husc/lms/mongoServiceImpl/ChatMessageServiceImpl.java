package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoEntity.ChatMessageStatus;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoRepository.ChatMessageStatusRepository;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

        private final ChatBoxRepository chatBoxRepo;
        private final ChatBoxMemberRepository memberRepo;
        private final ChatMessageRepository messageRepo;
        private final AccountRepository accountRepo;
        private final ChatMessageStatusRepository chatMessageStatusRepository;

        @Override
        public ChatMessage sendMessage(String chatBoxId, String senderAccount, String content) {
                Account acc = accountRepo.findByUsernameAndDeletedDateIsNull(senderAccount)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Sender account not found: " + senderAccount));

                ChatBox chatBox = chatBoxRepo.findById(chatBoxId)
                                .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND,
                                                "ChatBox not found with id: " + chatBoxId));

                ChatMessage message = ChatMessage.builder()
                                .chatBoxId(chatBoxId)
                                .senderAccount(senderAccount)
                                .content(content)
                                .createdAt(new Date())
                                .build();
                message = messageRepo.save(message);

                chatBox.setLastMessage(content);
                chatBox.setLastMessageAt(message.getCreatedAt());
                chatBox.setLastMessageBy(senderAccount);
                chatBoxRepo.save(chatBox);

                List<ChatBoxMember> members = memberRepo.findByChatBoxId(chatBoxId);
                if (members.isEmpty()) {
                        System.err.println(
                                        "Critical: No members found for ChatBox id: " + chatBoxId
                                                        + ". Cannot create message statuses.");
                        throw new AppException(ErrorCode.CHATBOX_MEMBER_NOT_FOUND,
                                        "No members in chatbox " + chatBoxId + ", cannot create statuses.");
                }

                List<ChatMessageStatus> statuses = new ArrayList<>();
                for (ChatBoxMember member : members) {
                        boolean isSender = member.getAccountUsername().equals(senderAccount);
                        ChatMessageStatus status = ChatMessageStatus.builder()
                                        .messageId(message.getId())
                                        .chatBoxId(chatBoxId)
                                        .accountUsername(member.getAccountUsername())
                                        .isRead(isSender)
                                        .readAt(isSender ? new Date() : null)
                                        .build();
                        statuses.add(status);
                }

                if (!statuses.isEmpty()) {
                        chatMessageStatusRepository.saveAll(statuses);
                        System.out.println(
                                        "[DEBUG] Saved " + statuses.size() + " message statuses for messageId: "
                                                        + message.getId());
                }

                return message;
        }

        @Override
        public Page<ChatMessage> getMessagesByChatBoxId(String chatBoxId, Pageable pageable) {
                return messageRepo.findByChatBoxId(chatBoxId, pageable);
        }

}
