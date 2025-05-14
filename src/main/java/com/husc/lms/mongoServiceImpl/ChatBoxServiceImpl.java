package com.husc.lms.mongoServiceImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatBoxServiceImpl implements ChatBoxService {
        private final ChatBoxRepository chatBoxRepo;
        private final ChatBoxMemberRepository memberRepo;
        private final AccountRepository accountRepo;

        private OffsetDateTime convertToOffsetDateTime(Date date) {
                if (date == null) {
                        return null;
                }
                return date.toInstant().atOffset(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
        }

        @Override
        public ChatBox createOrGetOneToOneChatBox(String currentUsername, String anotherUsername) {
                // Check if chatbox already exists
                List<ChatBox> existingChatBoxes = chatBoxRepo.findByIsGroupFalse();
                for (ChatBox chatBox : existingChatBoxes) {
                        List<ChatBoxMember> members = memberRepo.findByChatBoxId(chatBox.getId());
                        if (members.size() == 2) {
                                boolean hasCurrentUser = members.stream()
                                                .anyMatch(member -> member.getAccountUsername()
                                                                .equals(currentUsername));
                                boolean hasAnotherUser = members.stream()
                                                .anyMatch(member -> member.getAccountUsername()
                                                                .equals(anotherUsername));
                                if (hasCurrentUser && hasAnotherUser) {
                                        return chatBox;
                                }
                        }
                }

                // Create new chatbox if not exists
                Date now = new Date();
                ChatBox chatBox = ChatBox.builder()
                                .isGroup(false)
                                .createdAt(now)
                                .createdBy(currentUsername)
                                .memberAccountUsernames(Arrays.asList(currentUsername, anotherUsername))
                                .updatedAt(now)
                                .build();
                chatBox = chatBoxRepo.save(chatBox);

                // Create members
                List<ChatBoxMember> members = new ArrayList<>();
                members.add(ChatBoxMember.builder()
                                .chatBoxId(chatBox.getId())
                                .accountUsername(currentUsername)
                                .joinedAt(now)
                                .build());
                members.add(ChatBoxMember.builder()
                                .chatBoxId(chatBox.getId())
                                .accountUsername(anotherUsername)
                                .joinedAt(now)
                                .build());
                memberRepo.saveAll(members);

                return chatBox;
        }

        @Override
        public ChatBox createGroupChatBox(String name, String creatorUsername, String... memberUsernames) {
                Date now = new Date();
                List<String> allMembers = new ArrayList<>(Arrays.asList(memberUsernames));
                if (!allMembers.contains(creatorUsername)) {
                        allMembers.add(creatorUsername);
                }

                ChatBox chatBox = ChatBox.builder()
                                .isGroup(true)
                                .name(name)
                                .createdAt(now)
                                .createdBy(creatorUsername)
                                .memberAccountUsernames(allMembers)
                                .updatedAt(now)
                                .build();
                ChatBox saveChatBox = chatBoxRepo.save(chatBox);

                List<ChatBoxMember> members = allMembers.stream()
                                .map(username -> ChatBoxMember.builder()
                                                .chatBoxId(saveChatBox.getId())
                                                .accountUsername(username)
                                                .joinedAt(now)
                                                .build())
                                .toList();
                memberRepo.saveAll(members);

                return chatBox;
        }

        @Override
        public Page<ChatBox> getOneToOneChatBoxesForAccount(String accountUsername, Pageable pageable) {
                List<String> chatBoxIds = memberRepo.findByAccountUsername(accountUsername)
                                .stream()
                                .map(ChatBoxMember::getChatBoxId)
                                .toList();
                return chatBoxRepo.findByIdInAndIsGroupFalse(chatBoxIds, pageable);
        }

        @Override
        public Page<ChatBox> getAllChatBoxesForCurrentAccount(Pageable pageable) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String currentUsername = authentication.getName();

                List<String> chatBoxIds = memberRepo.findByAccountUsername(currentUsername)
                                .stream()
                                .map(ChatBoxMember::getChatBoxId)
                                .toList();
                return chatBoxRepo.findByIdIn(chatBoxIds, pageable);
        }

        @Override
        public Optional<ChatBox> getChatBoxById(String chatBoxId) {
                return chatBoxRepo.findById(chatBoxId);
        }

        @Override
        public List<ChatBoxMember> getChatBoxMembers(String chatBoxId) {
                return memberRepo.findByChatBoxId(chatBoxId);
        }
}
