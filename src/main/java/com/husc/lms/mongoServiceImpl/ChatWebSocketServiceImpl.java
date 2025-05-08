package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.mongoService.ChatWebSocketService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    private final ChatBoxService chatBoxService;
    private final ChatMessageService chatMessageService;
    private final AccountRepository accountRepository;

    @Override
    public ChatBoxCreateResponse handleChatCreation(ChatBoxCreateRequest request) {
        List<String> anotherAccounts = request.getAnotherAccounts();
        if (anotherAccounts == null || anotherAccounts.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_PARAMETER, "Danh sách tài khoản không được rỗng");
        }

        var context = SecurityContextHolder.getContext();
        String currentAccountUsername = context.getAuthentication().getName();
        Account currentAccount = accountRepository.findByUsernameAndDeletedDateIsNull(currentAccountUsername)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND, "Tài khoản hiện tại không tìm thấy"));

        String currentAccountFullname = currentAccount.getStudent() != null
                ? currentAccount.getStudent().getFullName()
                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getFullName()
                        : currentAccountUsername);
        String currentAccountAvatar = currentAccount.getStudent() != null
                ? currentAccount.getStudent().getAvatar()
                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getAvatar() : "");

        ChatBox chatBox;
        boolean isGroup;
        List<ChatBoxCreateResponse.ListOfMember> memberResponses = new ArrayList<>();

        if (anotherAccounts.size() == 1) {
            isGroup = false;
            String anotherAccountUsername = anotherAccounts.get(0);
            chatBox = chatBoxService.createOrGetOneToOneChatBox(currentAccountUsername, anotherAccountUsername);

            Account anotherUserDetails = accountRepository.findByUsernameAndDeletedDateIsNull(anotherAccountUsername)
                    .orElse(null);
            String anotherFullname = anotherUserDetails != null
                    ? (anotherUserDetails.getStudent() != null ? anotherUserDetails.getStudent().getFullName()
                            : (anotherUserDetails.getTeacher() != null ? anotherUserDetails.getTeacher().getFullName()
                                    : anotherAccountUsername))
                    : anotherAccountUsername;
            String anotherAvatar = anotherUserDetails != null
                    ? (anotherUserDetails.getStudent() != null ? anotherUserDetails.getStudent().getAvatar()
                            : (anotherUserDetails.getTeacher() != null ? anotherUserDetails.getTeacher().getAvatar()
                                    : ""))
                    : "";

            memberResponses.add(ChatBoxCreateResponse.ListOfMember.builder()
                    .memberAccountUsername(currentAccountUsername)
                    .memberFullname(currentAccountFullname)
                    .memberAvatar(currentAccountAvatar)
                    .build());
            memberResponses.add(ChatBoxCreateResponse.ListOfMember.builder()
                    .memberAccountUsername(anotherAccountUsername)
                    .memberFullname(anotherFullname)
                    .memberAvatar(anotherAvatar)
                    .build());

        } else {
            isGroup = true;
            if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_PARAMETER,
                        "Tên nhóm không được rỗng khi tạo nhóm chat nhiều người");
            }
            List<String> memberIdsForGroup = new ArrayList<>(anotherAccounts);
            if (!memberIdsForGroup.contains(currentAccountUsername)) {
                memberIdsForGroup.add(currentAccountUsername);
            }
            chatBox = chatBoxService.createGroupChatBox(request.getGroupName(), currentAccountUsername,
                    memberIdsForGroup.toArray(new String[0]));

            memberResponses.add(ChatBoxCreateResponse.ListOfMember.builder()
                    .memberAccountUsername(currentAccountUsername)
                    .memberFullname(currentAccountFullname)
                    .memberAvatar(currentAccountAvatar)
                    .build());
            for (String memberUsername : anotherAccounts) {
                if (!memberUsername.equals(currentAccountUsername)) {
                    Account memberAccDetails = accountRepository.findByUsernameAndDeletedDateIsNull(memberUsername)
                            .orElse(null);
                    String mFullname = memberAccDetails != null
                            ? (memberAccDetails.getStudent() != null ? memberAccDetails.getStudent().getFullName()
                                    : (memberAccDetails.getTeacher() != null
                                            ? memberAccDetails.getTeacher().getFullName()
                                            : memberUsername))
                            : memberUsername;
                    String mAvatar = memberAccDetails != null
                            ? (memberAccDetails.getStudent() != null ? memberAccDetails.getStudent().getAvatar()
                                    : (memberAccDetails.getTeacher() != null ? memberAccDetails.getTeacher().getAvatar()
                                            : ""))
                            : "";
                    memberResponses.add(ChatBoxCreateResponse.ListOfMember.builder()
                            .memberAccountUsername(memberUsername)
                            .memberFullname(mFullname)
                            .memberAvatar(mAvatar)
                            .build());
                }
            }
        }

        return ChatBoxCreateResponse.builder()
                .chatBoxId(chatBox.getId())
                .isGroup(isGroup)
                .createdBy(chatBox.getCreatedBy())
                .nameOfCreatedBy(currentAccountFullname)
                .createdAt(chatBox.getCreatedAt())
                .nameOfChatBox(chatBox.getName())
                .listMemmber(memberResponses)
                .build();
    }

    @Override
    public ChatMessageSenderResponse handleSendMessage(ChatMessageSenderRequest chatMessageSenderRequest) {
        ChatMessage chatMessage = chatMessageService.sendMessage(chatMessageSenderRequest.getChatBoxId(),
                chatMessageSenderRequest.getSenderAccount(), chatMessageSenderRequest.getContent());
        Account senderAccount = accountRepository
                .findByUsernameAndDeletedDateIsNull(chatMessageSenderRequest.getSenderAccount())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));

        String avatar = "";
        if (senderAccount.getStudent() != null) {
            avatar = senderAccount.getStudent().getAvatar();
        } else if (senderAccount.getTeacher() != null) {
            avatar = senderAccount.getTeacher().getAvatar();
        }

        return ChatMessageSenderResponse.builder()
                .id(chatMessage.getId())
                .chatBoxId(chatMessage.getChatBoxId())
                .senderAccount(chatMessage.getSenderAccount())
                .avatarSenderAccount(avatar)
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
