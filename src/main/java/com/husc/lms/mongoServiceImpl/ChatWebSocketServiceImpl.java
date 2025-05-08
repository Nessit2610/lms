package com.husc.lms.mongoServiceImpl;

import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
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
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    private final ChatBoxService chatBoxService;
    private final ChatMessageService chatMessageService;
    private final AccountRepository accountRepository;

    @Override
    public ChatBox handleChatCreation(ChatBoxCreateRequest request) {
        List<String> anotherAccounts = request.getAnotherAccounts();
        if (anotherAccounts == null || anotherAccounts.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_PARAMETER, "Danh sách tài khoản không được rỗng");
        }

        if (anotherAccounts.size() == 1) {
            // Tạo hoặc lấy chat 1-1
            return chatBoxService.createOrGetOneToOneChatBox(request.getCurrentAccount(), anotherAccounts.get(0));
        } else {
            // Tạo group chat
            // Kiểm tra groupName có được cung cấp không
            if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_PARAMETER,
                        "Tên nhóm không được rỗng khi tạo nhóm chat nhiều người");
            }
            // Thêm currentAccount vào danh sách memberIds nếu chưa có (để đảm bảo người tạo
            // cũng là thành viên)
            List<String> memberIds = new ArrayList<>(anotherAccounts);
            if (!memberIds.contains(request.getCurrentAccount())) {
                memberIds.add(request.getCurrentAccount());
            }
            return chatBoxService.createGroupChatBox(request.getGroupName(), request.getCurrentAccount(),
                    memberIds.toArray(new String[0]));
        }
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
