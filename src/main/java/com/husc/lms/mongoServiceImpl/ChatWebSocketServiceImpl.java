//package com.husc.lms.mongoServiceImpl;
//
//import java.time.OffsetDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.springframework.stereotype.Service;
//
//import com.husc.lms.entity.Account;
//import com.husc.lms.enums.ErrorCode;
//import com.husc.lms.exception.AppException;
//import com.husc.lms.mongoEntity.ChatBox;
//import com.husc.lms.mongoEntity.ChatMessage;
//import com.husc.lms.dto.request.ChatBoxCreateRequest;
//import com.husc.lms.dto.request.ChatMessageSenderRequest;
//import com.husc.lms.dto.response.ChatBoxCreateResponse;
//import com.husc.lms.dto.response.ChatMessageSenderResponse;
//import com.husc.lms.mongoService.ChatBoxService;
//import com.husc.lms.mongoService.ChatMessageService;
//import com.husc.lms.mongoService.ChatWebSocketService;
//import com.husc.lms.repository.AccountRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ChatWebSocketServiceImpl implements ChatWebSocketService {
//
//        private final ChatBoxService chatBoxService;
//        private final ChatMessageService chatMessageService;
//        private final AccountRepository accountRepository;
//
//        @Override
//        public ChatBoxCreateResponse handleChatCreation(ChatBoxCreateRequest request) {
//                System.out.println("[DEBUG] ChatWebSocketServiceImpl: handleChatCreation called with creator: "
//                                + request.getCurrentAccountUsername() + ", others: " + request.getAnotherAccounts());
//
//                String currentAccountUsername = request.getCurrentAccountUsername();
//                if (currentAccountUsername == null || currentAccountUsername.trim().isEmpty()) {
//                        System.err.println(
//                                        "[DEBUG] ChatWebSocketServiceImpl: currentAccountUsername is missing in handleChatCreation request.");
//                        throw new AppException(ErrorCode.INVALID_PARAMETER, "Current account username is required.");
//                }
//                Account currentAccount = accountRepository.findByUsernameAndDeletedDateIsNull(currentAccountUsername)
//                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
//                                                "Current account (creator) not found: " + currentAccountUsername));
//
//                String currentAccountFullname = currentAccount.getStudent() != null
//                                ? currentAccount.getStudent().getFullName()
//                                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getFullName()
//                                                : currentAccountUsername);
//                String currentAccountAvatar = currentAccount.getStudent() != null
//                                ? currentAccount.getStudent().getAvatar()
//                                : (currentAccount.getTeacher() != null ? currentAccount.getTeacher().getAvatar() : "");
//
//                ChatBox chatBoxEntity;
//                List<ChatBoxCreateResponse.ListOfMember> memberResponses = new ArrayList<>();
//                boolean isGroup = request.getAnotherAccounts().size() > 1;
//
//                if (request.getAnotherAccounts().size() == 1) {
//                        String anotherAccountUsername = request.getAnotherAccounts().get(0);
//                        if (currentAccountUsername.equals(anotherAccountUsername)) {
//                                throw new AppException(ErrorCode.INVALID_PARAMETER,
//                                                "Cannot create 1-on-1 chat with oneself.");
//                        }
//                        chatBoxEntity = chatBoxService.createOrGetOneToOneChatBox(currentAccountUsername,
//                                        anotherAccountUsername);
//                        Account anotherUserDetails = accountRepository
//                                        .findByUsernameAndDeletedDateIsNull(anotherAccountUsername).orElse(null);
//                        addMemberToResponse(memberResponses, currentAccount, anotherUserDetails);
//                } else {
//                        if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
//                                throw new AppException(ErrorCode.INVALID_PARAMETER,
//                                                "Group name is required for group chat.");
//                        }
//                        List<String> memberIdsForGroup = new ArrayList<>(request.getAnotherAccounts());
//                        if (!memberIdsForGroup.contains(currentAccountUsername)) {
//                                memberIdsForGroup.add(currentAccountUsername);
//                        }
//                        chatBoxEntity = chatBoxService.createGroupChatBox(request.getGroupName(),
//                                        currentAccountUsername, memberIdsForGroup.toArray(new String[0]));
//                        addMemberToResponse(memberResponses, currentAccount, null);
//                        for (String memberUsername : request.getAnotherAccounts()) {
//                                Account memberAccDetails = accountRepository
//                                                .findByUsernameAndDeletedDateIsNull(memberUsername).orElse(null);
//                                if (memberAccDetails != null)
//                                        addMemberToResponse(memberResponses, null, memberAccDetails);
//                        }
//                }
//
//                System.out.println("[DEBUG] ChatWebSocketServiceImpl: ChatBox processed/created. ID: "
//                                + chatBoxEntity.getId() +
//                                ", IsGroup: " + chatBoxEntity.isGroup() +
//                                ", Members in response: "
//                                + memberResponses.stream()
//                                                .map(ChatBoxCreateResponse.ListOfMember::getMemberAccountUsername)
//                                                .collect(Collectors.toList()));
//
//                // ChatBox.createdAt is now Date, DTO expects Date.
//                OffsetDateTime createdAtForResponse = chatBoxEntity.getCreatedAt();
//
//                return ChatBoxCreateResponse.builder()
//                                .chatBoxId(chatBoxEntity.getId())
//                                .isGroup(isGroup)
//                                .createdBy(chatBoxEntity.getCreatedBy())
//                                .nameOfCreatedBy(currentAccountFullname)
//                                .createdAt(createdAtForResponse) // Pass Date directly
//                                .nameOfChatBox(chatBoxEntity.getName())
//                                .listMemmber(memberResponses)
//                                .build();
//        }
//
//        private void addMemberToResponse(List<ChatBoxCreateResponse.ListOfMember> list, Account acc1, Account acc2) {
//                if (acc1 != null) {
//                        list.add(ChatBoxCreateResponse.ListOfMember.builder()
//                                        .memberAccountUsername(acc1.getUsername())
//                                        .memberFullname(acc1.getStudent() != null ? acc1.getStudent().getFullName()
//                                                        : (acc1.getTeacher() != null ? acc1.getTeacher().getFullName()
//                                                                        : acc1.getUsername()))
//                                        .memberAvatar(acc1.getStudent() != null ? acc1.getStudent().getAvatar()
//                                                        : (acc1.getTeacher() != null ? acc1.getTeacher().getAvatar()
//                                                                        : ""))
//                                        .build());
//                }
//                if (acc2 != null) {
//                        list.add(ChatBoxCreateResponse.ListOfMember.builder()
//                                        .memberAccountUsername(acc2.getUsername())
//                                        .memberFullname(acc2.getStudent() != null ? acc2.getStudent().getFullName()
//                                                        : (acc2.getTeacher() != null ? acc2.getTeacher().getFullName()
//                                                                        : acc2.getUsername()))
//                                        .memberAvatar(acc2.getStudent() != null ? acc2.getStudent().getAvatar()
//                                                        : (acc2.getTeacher() != null ? acc2.getTeacher().getAvatar()
//                                                                        : ""))
//                                        .build());
//                }
//        }
//
//        @Override
//        public ChatMessageSenderResponse handleSendMessage(ChatMessageSenderRequest chatMessageSenderRequest) {
//                System.out.println("[DEBUG] ChatWebSocketServiceImpl: handleSendMessage called for chatBoxId: " +
//                                chatMessageSenderRequest.getChatBoxId() + ", Sender: "
//                                + chatMessageSenderRequest.getSenderAccount());
//
//                ChatMessage chatMessage = chatMessageService.sendMessage(
//                                chatMessageSenderRequest.getChatBoxId(),
//                                chatMessageSenderRequest.getSenderAccount(),
//                                chatMessageSenderRequest.getContent());
//
//                if (chatMessage == null) {
//                        System.err.println(
//                                        "[DEBUG] ChatWebSocketServiceImpl: chatMessageService.sendMessage returned null. Message sending potentially failed or was handled with only syserr in service.");
//                        return null;
//                }
//
//                Account senderAccountDetails = accountRepository
//                                .findByUsernameAndDeletedDateIsNull(chatMessage.getSenderAccount()).orElse(null);
//                String avatar = "";
//                if (senderAccountDetails != null) {
//                        avatar = senderAccountDetails.getStudent() != null
//                                        ? senderAccountDetails.getStudent().getAvatar()
//                                        : (senderAccountDetails.getTeacher() != null
//                                                        ? senderAccountDetails.getTeacher().getAvatar()
//                                                        : "");
//                }
//
//                System.out.println("[DEBUG] ChatWebSocketServiceImpl: Message sent. ID: " + chatMessage.getId() +
//                                ", ChatBoxID: " + chatMessage.getChatBoxId() + ", Sender: "
//                                + chatMessage.getSenderAccount());
//
//                OffsetDateTime createdAtForResponse = chatMessage.getCreatedAt();
//
//                return ChatMessageSenderResponse.builder()
//                                .id(chatMessage.getId())
//                                .chatBoxId(chatMessage.getChatBoxId())
//                                .senderAccount(chatMessage.getSenderAccount())
//                                .avatarSenderAccount(avatar)
//                                .content(chatMessage.getContent())
//                                .createdAt(createdAtForResponse) // Pass Date directly
//                                .build();
//        }
//}
