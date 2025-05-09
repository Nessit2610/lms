package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public ChatBox createOrGetOneToOneChatBox(String accountId1, String accountId2) {
            // Tìm kiếm chatbox giữa 2 account đã tồn tại chưa
            List<ChatBoxMember> members1 = memberRepo.findByAccountUsername(accountId1);
            List<ChatBoxMember> members2 = memberRepo.findByAccountUsername(accountId2);

            for (ChatBoxMember member1 : members1) {
                    for (ChatBoxMember member2 : members2) {
                            if (member1.getChatBoxId().equals(member2.getChatBoxId())) {
                                    Optional<ChatBox> existingChatBox = chatBoxRepo
                                                    .findById(member1.getChatBoxId());
                                    if (existingChatBox.isPresent() && !existingChatBox.get().isGroup()) {
                                            return existingChatBox.get();
                                    }
                            }
                    }
            }

            // Tạo chatbox mới nếu chưa tồn tại
            ChatBox newChatBox = ChatBox.builder()
                            .isGroup(false)
                            .createdAt(new Date())
                            .createdBy(accountId1)
                            .build();
            newChatBox = chatBoxRepo.save(newChatBox);

            // Thêm account vào chatbox
            ChatBoxMember member1 = ChatBoxMember.builder()
                            .chatBoxId(newChatBox.getId())
                            .accountUsername(accountId1)
                            .joinedAt(new Date())
                            .build();

            ChatBoxMember member2 = ChatBoxMember.builder()
                            .chatBoxId(newChatBox.getId())
                            .accountUsername(accountId2)
                            .joinedAt(new Date())
                            .build();

            memberRepo.saveAll(Arrays.asList(member1, member2));

            return newChatBox;
    }

    @Override
    public ChatBox createGroupChatBox(String name, String createdBy, String... accountIds) {
            ChatBox newChatBox = ChatBox.builder()
                            .isGroup(true)
                            .name(name)
                            .createdAt(new Date())
                            .createdBy(createdBy)
                            .build();
            newChatBox = chatBoxRepo.save(newChatBox);

            List<ChatBoxMember> members = new ArrayList<>();
            for (String accountId : accountIds) {
                    ChatBoxMember member = ChatBoxMember.builder()
                                    .chatBoxId(newChatBox.getId())
                                    .accountUsername(accountId)
                                    .joinedAt(new Date())
                                    .build();
                    members.add(member);
            }
            memberRepo.saveAll(members);

            return newChatBox;
    }

    @Override
    public Page<ChatBox> getOneToOneChatBoxesForAccount(Pageable pageable) {
            var context = SecurityContextHolder.getContext();
            String username = context.getAuthentication().getName();

            Account currentAccount = accountRepo.findByUsernameAndDeletedDateIsNull(username)
                            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));

            List<ChatBoxMember> memberships = memberRepo.findByAccountUsername(currentAccount.getUsername());
            List<String> chatBoxIds = memberships.stream()
                            .map(ChatBoxMember::getChatBoxId)
                            .distinct()
                            .toList();

            return chatBoxRepo.findByIdInAndIsGroupFalse(chatBoxIds, pageable);
    }

    @Override
    public Page<ChatBox> getAllChatBoxesForCurrentAccount(Pageable pageable) {
            var context = SecurityContextHolder.getContext();
            String username = context.getAuthentication().getName();

            Account currentAccount = accountRepo.findByUsernameAndDeletedDateIsNull(username)
                            .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));

            // Lấy tất cả chatbox mà user tham gia
            List<ChatBoxMember> memberships = memberRepo.findByAccountUsername(currentAccount.getUsername());
            List<String> chatBoxIds = memberships.stream()
                            .map(ChatBoxMember::getChatBoxId)
                            .distinct()
                            .toList();

            // Trả về tất cả chatbox (cả 1-1 và group)
            return chatBoxRepo.findByIdIn(chatBoxIds, pageable);
    }

}
