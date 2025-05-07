//package com.husc.lms.mongoServiceImpl;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import com.husc.lms.entity.Account;
//import com.husc.lms.mongoEntity.ChatBox;
//import com.husc.lms.mongoEntity.ChatBoxMember;
//import com.husc.lms.mongoEntity.ChatMessage;
//import com.husc.lms.mongoEntity.ChatMessageStatus;
//import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
//import com.husc.lms.mongoRepository.ChatBoxRepository;
//import com.husc.lms.mongoRepository.ChatMessageRepository;
//import com.husc.lms.mongoService.ChatBoxService;
//import com.husc.lms.mongoService.ChatMessageService;
//import com.husc.lms.repository.AccountRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ChatServiceImpl implements ChatBoxService, ChatMessageService {
//
//    private final ChatBoxRepository chatBoxRepo;
//    private final ChatBoxMemberRepository memberRepo;
//    private final ChatMessageRepository messageRepo;
//    private final AccountRepository accountRepo;
//
//    @Override
//    public ChatBox createOrGetOneToOneChatBox(String accountId1, String accountId2) {
//        // Find existing chat box between two users
//        List<ChatBoxMember> members1 = memberRepo.findByAccountId(accountId1);
//        List<ChatBoxMember> members2 = memberRepo.findByAccountId(accountId2);
//
//        for (ChatBoxMember member1 : members1) {
//            for (ChatBoxMember member2 : members2) {
//                if (member1.getChatBoxId().equals(member2.getChatBoxId())) {
//                    Optional<ChatBox> existingChatBox = chatBoxRepo.findById(member1.getChatBoxId());
//                    if (existingChatBox.isPresent() && !existingChatBox.get().isGroup()) {
//                        return existingChatBox.get();
//                    }
//                }
//            }
//        }
//
//        // Create new chat box if not exists
//        ChatBox newChatBox = ChatBox.builder()
//                .isGroup(false)
//                .createdAt(new Date())
//                .createdBy(accountId1)
//                .build();
//        newChatBox = chatBoxRepo.save(newChatBox);
//
//        // Add members
//        ChatBoxMember member1 = ChatBoxMember.builder()
//                .chatBoxId(newChatBox.getId())
//                .accountId(accountId1)
//                .joinedAt(new Date())
//                .build();
//
//        ChatBoxMember member2 = ChatBoxMember.builder()
//                .chatBoxId(newChatBox.getId())
//                .accountId(accountId2)
//                .joinedAt(new Date())
//                .build();
//
//        memberRepo.saveAll(Arrays.asList(member1, member2));
//
//        return newChatBox;
//    }
//
//    @Override
//    public ChatBox createGroupChatBox(String name, String createdBy, String... accountIds) {
//        ChatBox newChatBox = ChatBox.builder()
//                .isGroup(true)
//                .name(name)
//                .createdAt(new Date())
//                .createdBy(createdBy)
//                .build();
//        newChatBox = chatBoxRepo.save(newChatBox);
//
//        List<ChatBoxMember> members = new ArrayList<>();
//        for (String accountId : accountIds) {
//            ChatBoxMember member = ChatBoxMember.builder()
//                    .chatBoxId(newChatBox.getId())
//                    .accountId(accountId)
//                    .joinedAt(new Date())
//                    .build();
//            members.add(member);
//        }
//        memberRepo.saveAll(members);
//
//        return newChatBox;
//    }
//
//    @Override
//    public ChatMessage sendMessage(String chatBoxId, String senderId, String content) {
//        // Create and save message
//        ChatMessage message = ChatMessage.builder()
//                .chatBoxId(chatBoxId)
//                .senderId(senderId)
//                .content(content)
//                .createdAt(new Date())
//                .build();
//        message = messageRepo.save(message);
//
//        // Get all members except sender
//        List<ChatBoxMember> members = memberRepo.findByChatBoxId(chatBoxId);
//        List<ChatMessageStatus> statuses = new ArrayList<>();
//
//        // Create message status for each member
//        for (ChatBoxMember member : members) {
//            ChatMessageStatus status = ChatMessageStatus.builder()
//                    .messageId(message.getId())
//                    .accountId(member.getAccountId())
//                    .isRead(member.getAccountId().equals(senderId)) // true for sender, false for others
//                    .readAt(member.getAccountId().equals(senderId) ? new Date() : null)
//                    .build();
//            statuses.add(status);
//        }
//
//        return message;
//    }
//
//    @Override
//    public Page<ChatBox> getOneToOneChatBoxesForAccount(Pageable pageable) {
//        var context = SecurityContextHolder.getContext();
//        String username = context.getAuthentication().getName();
//
//        Account currentAccount = accountRepo.findByUsernameAndDeletedDateIsNull(username).orElseThrow();
//
//        List<ChatBoxMember> memberships = memberRepo.findByAccountId(currentAccount.getId());
//        List<String> chatBoxIds = memberships.stream()
//                .map(ChatBoxMember::getChatBoxId)
//                .distinct()
//                .toList();
//
//        return chatBoxRepo.findByIdInAndIsGroupFalse(chatBoxIds, pageable);
//    }
//
//}
