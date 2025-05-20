package com.husc.lms.mongoServiceImpl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.husc.lms.entity.Account;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.dto.request.ChatBoxAddMemberRequest;
import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxAddMemberResponse;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.mongoService.ChatWebSocketService;
import com.husc.lms.mongoService.ChatBoxMemberService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

        private final ChatBoxService chatBoxService;
        private final ChatMessageService chatMessageService;
        private final AccountRepository accountRepo;
        private final ChatBoxMemberService chatBoxMemberService;
        private final SimpMessagingTemplate messagingTemplate;

        private OffsetDateTime convertToOffsetDateTime(java.util.Date date) {
                if (date == null) {
                        return null;
                }
                // Using system default zone. Consider if a specific zone like UTC is more
                // appropriate.
                return date.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }

        private Date convertToDate(OffsetDateTime offsetDateTime) {
                if (offsetDateTime == null) {
                        return null;
                }
                return Date.from(offsetDateTime.toInstant());
        }

        @Override
        public ChatBoxCreateResponse handleChatCreation(ChatBoxCreateRequest request) {

                String username = SecurityContextHolder.getContext().getAuthentication().getName();

                System.out.println("Username Account: " + username);
                System.out.println("[DEBUG] ChatWebSocketServiceImpl: handleChatCreation called with creator: "
                                + request.getCurrentAccountUsername() + ", others: " + request.getAnotherAccounts());

                String currentAccountUsername = request.getCurrentAccountUsername();
                if (currentAccountUsername == null || currentAccountUsername.trim().isEmpty()) {
                        System.err.println(
                                        "[DEBUG] ChatWebSocketServiceImpl: currentAccountUsername is missing in handleChatCreation request.");
                        throw new AppException(ErrorCode.INVALID_PARAMETER, "Current account username is required.");
                }
                Account currentAccount = accountRepo.findByUsernameAndDeletedDateIsNull(currentAccountUsername)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Current account (creator) not found: " + currentAccountUsername));

                String currentAccountFullname = currentAccount.getStudent() != null
                                ? currentAccount.getStudent().getFullName()
                                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getFullName()
                                                : currentAccountUsername);
                String currentAccountAvatar = currentAccount.getStudent() != null
                                ? currentAccount.getStudent().getAvatar()
                                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getAvatar() : "");

                ChatBox chatBoxEntity;
                List<ChatBoxCreateResponse.ListOfMember> memberResponses = new ArrayList<>();
                boolean isGroup = request.getAnotherAccounts().size() > 1;

                if (request.getAnotherAccounts().size() == 1) {
                        String anotherAccountUsername = request.getAnotherAccounts().get(0);
                        if (currentAccountUsername.equals(anotherAccountUsername)) {
                                throw new AppException(ErrorCode.INVALID_PARAMETER,
                                                "Cannot create 1-on-1 chat with oneself.");
                        }
                        chatBoxEntity = chatBoxService.createOrGetOneToOneChatBox(currentAccountUsername,
                                        anotherAccountUsername);
                        Account anotherUserDetails = accountRepo
                                        .findByUsernameAndDeletedDateIsNull(anotherAccountUsername).orElse(null);
                        addMemberToResponse(memberResponses, currentAccount, anotherUserDetails);
                } else {
                        if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
                                throw new AppException(ErrorCode.INVALID_PARAMETER,
                                                "Group name is required for group chat.");
                        }
                        List<String> memberIdsForGroup = new ArrayList<>(request.getAnotherAccounts());
                        if (!memberIdsForGroup.contains(currentAccountUsername)) {
                                memberIdsForGroup.add(currentAccountUsername);
                        }
                        chatBoxEntity = chatBoxService.createGroupChatBox(request.getGroupName(),
                                        currentAccountUsername, memberIdsForGroup.toArray(new String[0]));
                        addMemberToResponse(memberResponses, currentAccount, null);
                        for (String memberUsername : request.getAnotherAccounts()) {
                                Account memberAccDetails = accountRepo
                                                .findByUsernameAndDeletedDateIsNull(memberUsername).orElse(null);
                                if (memberAccDetails != null)
                                        addMemberToResponse(memberResponses, null, memberAccDetails);
                        }
                }

                System.out.println("[DEBUG] ChatWebSocketServiceImpl: ChatBox processed/created. ID: "
                                + chatBoxEntity.getId() +
                                ", IsGroup: " + chatBoxEntity.isGroup() +
                                ", Members in response: "
                                + memberResponses.stream()
                                                .map(ChatBoxCreateResponse.ListOfMember::getAccountUsername)
                                                .collect(Collectors.toList()));

                ChatBoxCreateResponse response = ChatBoxCreateResponse.builder()
                                .chatBoxId(chatBoxEntity.getId())
                                .isGroup(isGroup)
                                .createdBy(chatBoxEntity.getCreatedBy())
                                .nameOfCreatedBy(currentAccountFullname)
                                .createdAt(convertToOffsetDateTime(chatBoxEntity.getCreatedAt()))
                                .nameOfChatBox(chatBoxEntity.getName())
                                .listMemmber(memberResponses)
                                .build();

                // Gửi realtime cho người tạo
                messagingTemplate.convertAndSend("/topic/chatbox/" + currentAccountUsername + "/created", response);
                // Nếu là chat 1-1, gửi cho người còn lại
                if (!isGroup && request.getAnotherAccounts().size() == 1) {
                        String anotherUsername = request.getAnotherAccounts().get(0);
                        messagingTemplate.convertAndSend("/topic/chatbox/" + anotherUsername + "/created", response);
                }

                return response;
        }

        private void addMemberToResponse(List<ChatBoxCreateResponse.ListOfMember> list, Account acc1, Account acc2) {
                if (acc1 != null) {
                        list.add(ChatBoxCreateResponse.ListOfMember.builder()
                                        .accountUsername(acc1.getUsername())
                                        .accountFullname(acc1.getStudent() != null ? acc1.getStudent().getFullName()
                                                        : (acc1.getTeacher() != null ? acc1.getTeacher().getFullName()
                                                                        : acc1.getUsername()))
                                        .avatar(acc1.getStudent() != null ? acc1.getStudent().getAvatar()
                                                        : (acc1.getTeacher() != null ? acc1.getTeacher().getAvatar()
                                                                        : ""))
                                        .build());
                }
                if (acc2 != null) {
                        list.add(ChatBoxCreateResponse.ListOfMember.builder()
                                        .accountUsername(acc2.getUsername())
                                        .accountFullname(acc2.getStudent() != null ? acc2.getStudent().getFullName()
                                                        : (acc2.getTeacher() != null ? acc2.getTeacher().getFullName()
                                                                        : acc2.getUsername()))
                                        .avatar(acc2.getStudent() != null ? acc2.getStudent().getAvatar()
                                                        : (acc2.getTeacher() != null ? acc2.getTeacher().getAvatar()
                                                                        : ""))
                                        .build());
                }
        }

        @Override
        public ChatMessageSenderResponse handleSendMessage(ChatMessageSenderRequest chatMessageSenderRequest) {
                String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                System.out.println("Username Account: " + currentUsername);
                System.out.println("[DEBUG] ChatWebSocketServiceImpl: handleSendMessage called for chatBoxId: " +
                                chatMessageSenderRequest.getChatBoxId() + ", Sender: "
                                + chatMessageSenderRequest.getSenderAccount());

                // 1. Lưu tin nhắn vào DB (đã có)
                ChatMessage chatMessage = chatMessageService.sendMessage(
                                chatMessageSenderRequest.getChatBoxId(),
                                chatMessageSenderRequest.getSenderAccount(),
                                chatMessageSenderRequest.getContent(),
                                chatMessageSenderRequest.getFile(),
                                chatMessageSenderRequest.getFileType());

                if (chatMessage == null) {
                        System.err.println(
                                        "[DEBUG] ChatWebSocketServiceImpl: chatMessageService.sendMessage returned null. Message sending potentially failed or was handled with only syserr in service.");
                        // Có thể ném lỗi hoặc trả về một response lỗi cụ thể nếu cần
                        return null;
                }

                // 2. Chuẩn bị response (đã có)
                Account senderAccountDetails = accountRepo
                                .findByUsernameAndDeletedDateIsNull(chatMessage.getSenderAccount()).orElse(null);
                String avatar = "";
                if (senderAccountDetails != null) {
                        avatar = senderAccountDetails.getStudent() != null
                                        ? senderAccountDetails.getStudent().getAvatar()
                                        : (senderAccountDetails.getTeacher() != null
                                                        ? senderAccountDetails.getTeacher().getAvatar()
                                                        : "");
                }

                System.out.println("[DEBUG] ChatWebSocketServiceImpl: Message saved. ID: " + chatMessage.getId() +
                                ", ChatBoxID: " + chatMessage.getChatBoxId() + ", Sender: "
                                + chatMessage.getSenderAccount());

                ChatMessageSenderResponse response = ChatMessageSenderResponse.builder()
                                .id(chatMessage.getId())
                                .chatBoxId(chatMessage.getChatBoxId())
                                .senderAccount(chatMessage.getSenderAccount())
                                .avatarSenderAccount(avatar)
                                .content(chatMessage.getContent())
                                .createdAt(convertToOffsetDateTime(chatMessage.getCreatedAt()))
                                .path(chatMessage.getPath())
                                .type(chatMessage.getType())
                                .filename(chatMessage.getFilename())
                                .build();

                // 3. Gửi response đến topic chung của chat box
                String destination = "/topic/chatbox/" + chatMessage.getChatBoxId();
                System.out.println("[DEBUG] ChatWebSocketServiceImpl: Broadcasting message to " + destination
                                + " with payload: " + response.toString());
                messagingTemplate.convertAndSend(destination, response);

                System.out.println(
                                "[DEBUG] ChatWebSocketServiceImpl: Returning ChatMessageSenderResponse for potential @SendToUser: "
                                                + response.toString());
                return response;
        }

        @Override
        public ChatBoxAddMemberResponse handleAddMembersToChatbox(ChatBoxAddMemberRequest chatBoxAddMemberRequest) {
                if (chatBoxAddMemberRequest == null || chatBoxAddMemberRequest.getChatboxId() == null ||
                                chatBoxAddMemberRequest.getUsernameOfRequestor() == null ||
                                chatBoxAddMemberRequest.getChatMemberRequests() == null
                                || chatBoxAddMemberRequest.getChatMemberRequests().isEmpty() ||
                                chatBoxAddMemberRequest.getChatMemberRequests().get(0).getMemberAccount() == null) {
                        throw new AppException(ErrorCode.INVALID_PARAMETER,
                                        "Yêu cầu thêm thành viên không hợp lệ, thiếu thông tin.");
                }

                // Assuming we are adding one member at a time as per the original service
                // signature
                String usernameOfMemberToAdd = chatBoxAddMemberRequest.getChatMemberRequests().get(0)
                                .getMemberAccount();
                String requestor = chatBoxAddMemberRequest.getUsernameOfRequestor();
                String chatBoxId = chatBoxAddMemberRequest.getChatboxId();

                System.out.println("[DEBUG] ChatWebSocketServiceImpl: handleAddMembersToChatbox called for ChatBoxId: "
                                + chatBoxId + ", MemberToAdd: " + usernameOfMemberToAdd
                                + ", Requestor: " + requestor);

                ChatBox updatedOrNewChatBox = chatBoxMemberService.addMemberToChatBox(
                                chatBoxId,
                                usernameOfMemberToAdd,
                                requestor);

                if (updatedOrNewChatBox == null) {
                        System.err.println(
                                        "[DEBUG] ChatWebSocketServiceImpl: chatBoxMemberService.addMemberToChatBox returned null.");
                        return null;
                }

                List<ChatBoxAddMemberResponse.ChatMemberResponse> memberResponses = updatedOrNewChatBox
                                .getMemberAccountUsernames().stream()
                                .map(username -> {
                                        Account acc = accountRepo.findByUsernameAndDeletedDateIsNull(username)
                                                        .orElse(null);
                                        String fullName = username;
                                        String avatar = "";
                                        Date joinedAt = null; // Placeholder, ideally fetch from ChatBoxMember entity

                                        if (acc != null) {
                                                fullName = acc.getStudent() != null ? acc.getStudent().getFullName()
                                                                : (acc.getTeacher() != null
                                                                                ? acc.getTeacher().getFullName()
                                                                                : username);
                                                avatar = acc.getStudent() != null ? acc.getStudent().getAvatar()
                                                                : (acc.getTeacher() != null
                                                                                ? acc.getTeacher().getAvatar()
                                                                                : "");
                                        }
                                        if (username.equals(usernameOfMemberToAdd) && !updatedOrNewChatBox.getId()
                                                        .equals(chatBoxAddMemberRequest.getChatboxId())) {

                                                joinedAt = new Date();
                                        } else {
                                                joinedAt = updatedOrNewChatBox.getUpdatedAt(); // Or null, or fetch
                                                // actual join date
                                        }

                                        return ChatBoxAddMemberResponse.ChatMemberResponse.builder()
                                                        .memberAccount(username)
                                                        .memberName(fullName)
                                                        .memberAvatar(avatar)
                                                        .joinAt(joinedAt)
                                                        // .chatMemberId( ) // Needs ID from ChatBoxMember entity
                                                        .build();
                                })
                                .collect(Collectors.toList());

                ChatBoxAddMemberResponse response = ChatBoxAddMemberResponse.builder()
                                .chatboxId(updatedOrNewChatBox.getId()) // Corrected field name for response DTO
                                .chatboxName(updatedOrNewChatBox.getName()) // Corrected field name for response DTO
                                .chatMemberReponses(memberResponses) // Corrected field name
                                .build();
                System.out.println("[DEBUG] ChatWebSocketServiceImpl: Returning ChatBoxAddMemberResponse: "
                                + response.toString());
                return response;
        }

}
