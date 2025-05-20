package com.husc.lms.mongoServiceImpl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.ChatBoxResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.service.OffsetLimitPageRequest;

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
    public Page<ChatBoxResponse> getAllChatBoxesForCurrentAccount(int pageNumber, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<String> chatBoxIds = memberRepo.findByAccountUsername(currentUsername)
                        .stream()
                        .map(ChatBoxMember::getChatBoxId)
                        .collect(Collectors.toList());

        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be 1 or greater.");
        }

        int actualOffset = pageNumber;
        int actualLimit = pageSize + 1;

        Sort sort = Sort.by(Sort.Direction.DESC, "lastMessageAt");

        Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

        Page<ChatBox> fetchedChatBoxesPage = chatBoxRepo.findByIdIn(chatBoxIds, fetchPageable);
        List<ChatBox> fetchedContent = fetchedChatBoxesPage.getContent();

        boolean hasNext = fetchedContent.size() > pageSize;
        List<ChatBox> chatBoxesToReturn = hasNext ? fetchedContent.subList(0, pageSize) : fetchedContent;

        Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

        List<ChatBoxResponse> chatBoxResponses = chatBoxesToReturn.stream().map(chatBox -> {
            List<String> memberUsernamesList = chatBox.getMemberAccountUsernames();
            List<ChatBoxResponse.MemberAccountInChatBox> memberAccounts = new ArrayList<>();
            if (memberUsernamesList != null) {
                memberAccounts = memberUsernamesList.stream()
                                .map(username -> {
                                    Account account = accountRepo.findByUsernameAndDeletedDateIsNull(username)
                                                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
                                    return ChatBoxResponse.MemberAccountInChatBox.builder()
                                                .accountId(String.valueOf(account.getId()))
                                                .acountUsername(username)
                                                .accountFullname(account.getStudent() != null
                                                                ? account.getStudent().getFullName()
                                                                : account.getTeacher() != null
                                                                    ? account.getTeacher().getFullName()
                                                                    : "")
                                                .avatar(account.getStudent() != null
                                                                ? account.getStudent().getAvatar()
                                                                : account.getTeacher() != null
                                                                    ? account.getTeacher().getAvatar()
                                                                    : "")
                                                .build();
                                }).collect(Collectors.toList());
                }
                return ChatBoxResponse.builder()
                                .id(chatBox.getId())
                                .isGroup(chatBox.isGroup())
                                .createdAt(chatBox.getCreatedAt())
                                .createdBy(chatBox.getCreatedBy())
                                .name(chatBox.getName())
                                .updatedAt(chatBox.getUpdatedAt())
                                .lastMessage(chatBox.getLastMessage())
                                .lastMessageAt(chatBox.getLastMessageAt())
                                .lastMessageBy(chatBox.getLastMessageBy())
                                .memberAccountUsernames(memberAccounts)
                                .build();
        }).collect(Collectors.toList());

        long totalElements = fetchedChatBoxesPage.getTotalElements();
        return new PageImpl<>(chatBoxResponses, returnPageable, totalElements);
    }

    @Override
    public Optional<ChatBox> getChatBoxById(String chatBoxId) {
            return chatBoxRepo.findById(chatBoxId);
    }

    @Override
    public List<ChatBoxMember> getChatBoxMembers(String chatBoxId) {
            return memberRepo.findByChatBoxId(chatBoxId);
    }

    @Override
    public ChatBox renameChatBox(String chatBoxId, String newName) {
            // Lấy thông tin người dùng hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            // Kiểm tra chatbox tồn tại
            ChatBox chatBox = chatBoxRepo.findById(chatBoxId)
                            .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND));

            // Kiểm tra quyền đổi tên
            if (!chatBox.getCreatedBy().equals(currentUsername)) {
                    throw new AppException(ErrorCode.FORBIDDEN);
            }

            // Cập nhật tên mới
            chatBoxRepo.updateNameById(chatBoxId, newName);

            // Lấy và trả về chatbox đã cập nhật
            return chatBoxRepo.findById(chatBoxId).orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND));
    }

    @Override
    public Page<ChatBoxResponse> searchByNameOfChatBox(String nameKeyword, int pageNumber, int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (pageSize < 1) {
                throw new IllegalArgumentException("pageSize must be 1 or greater.");
        }

        int actualOffset = pageNumber;
        int actualLimit = pageSize + 1;

        Sort sort = Sort.by(Sort.Direction.DESC, "lastMessageAt");
        Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

        // Tạo regex cho tìm kiếm tương đối, không phân biệt hoa thường
        String nameRegex = ".*" + java.util.regex.Pattern.quote(nameKeyword) + ".*";

        Page<ChatBox> fetchedChatBoxesPage = chatBoxRepo.findGroupChatsByMemberAndNameLike(currentUsername,
                        nameRegex, fetchPageable);
        List<ChatBox> fetchedContent = fetchedChatBoxesPage.getContent();

        boolean hasNext = fetchedContent.size() > pageSize;
        List<ChatBox> chatBoxesToReturn = hasNext ? fetchedContent.subList(0, pageSize) : fetchedContent;

        Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

        List<ChatBoxResponse> chatBoxResponses = chatBoxesToReturn.stream().map(chatBox -> {
                List<String> memberUsernamesList = chatBox.getMemberAccountUsernames();
                List<ChatBoxResponse.MemberAccountInChatBox> memberAccounts = new ArrayList<>();
                if (memberUsernamesList != null) {
                    memberAccounts = memberUsernamesList.stream()
                                    .map(username -> {
                                        Account account = accountRepo.findByUsernameAndDeletedDateIsNull(username)
                                                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
                                        return ChatBoxResponse.MemberAccountInChatBox.builder()
                                                        .accountId(String.valueOf(account.getId()))
                                                        .acountUsername(username)
                                                        .accountFullname(account.getStudent() != null
                                                                        ? account.getStudent().getFullName()
                                                                        : account.getTeacher() != null
                                                                            ? account.getTeacher().getFullName()
                                                                            : "")
                                                        .avatar(account.getStudent() != null
                                                                        ? account.getStudent().getAvatar()
                                                                        : account.getTeacher() != null
                                                                            ? account.getTeacher().getAvatar()
                                                                            : "")
                                                        .build();
                                    }).collect(Collectors.toList());
                }
                return ChatBoxResponse.builder()
                                .id(chatBox.getId())
                                .isGroup(chatBox.isGroup())
                                .createdAt(chatBox.getCreatedAt())
                                .createdBy(chatBox.getCreatedBy())
                                .name(chatBox.getName())
                                .updatedAt(chatBox.getUpdatedAt())
                                .lastMessage(chatBox.getLastMessage())
                                .lastMessageAt(chatBox.getLastMessageAt())
                                .lastMessageBy(chatBox.getLastMessageBy())
                                .memberAccountUsernames(memberAccounts)
                                .build();
        }).collect(Collectors.toList());

        long totalElements = fetchedChatBoxesPage.getTotalElements();
        return new PageImpl<>(chatBoxResponses, returnPageable, totalElements);
    }
}
