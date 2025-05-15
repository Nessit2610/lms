package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoRepository.ChatBoxMemberRepository;
import com.husc.lms.mongoRepository.ChatBoxRepository;
import com.husc.lms.mongoService.ChatBoxMemberService;
import com.husc.lms.mongoService.ChatBoxService; // Assuming you might want to reuse chat box creation logic
import com.husc.lms.repository.AccountRepository; // To validate users


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatBoxMemberServiceImpl implements ChatBoxMemberService {

    private final ChatBoxRepository chatBoxRepository;
    private final ChatBoxMemberRepository chatBoxMemberRepository;
    private final AccountRepository accountRepository;
    private final ChatBoxService chatBoxService;

    @Override
    @Transactional
    public ChatBox addMemberToChatBox(String chatBoxId, String usernameOfMemberToAdd, String usernameOfRequestor) {
        accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfRequestor)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                        "Tài khoản yêu cầu không tìm thấy: " + usernameOfRequestor));
        accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfMemberToAdd)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                        "Tài khoản cần thêm không tìm thấy: " + usernameOfMemberToAdd));
        ChatBox currentChatBox = chatBoxRepository.findById(chatBoxId)
                .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND,
                        "ChatBox không tìm thấy với ID: " + chatBoxId));

        if (!currentChatBox.isGroup()) {
            List<String> existingMembers = new ArrayList<>(currentChatBox.getMemberAccountUsernames());
            if (!existingMembers.contains(usernameOfMemberToAdd)) {
                existingMembers.add(usernameOfMemberToAdd);
            }
            if (!existingMembers.contains(usernameOfRequestor)) {
                existingMembers.add(usernameOfRequestor);
            }

            List<String> finalMemberUsernames = existingMembers.stream().distinct().collect(Collectors.toList());

            if (finalMemberUsernames.size() < 3) {
                throw new AppException(ErrorCode.INVALID_OPERATION,
                        "Không thể tạo nhóm với ít hơn 3 thành viên từ chat 1-1.");
            }
            String newGroupName = "Nhóm: " + finalMemberUsernames.stream().limit(3).collect(Collectors.joining(", "));

            ChatBox newGroupChatBox = chatBoxService.createGroupChatBox(newGroupName, usernameOfRequestor,
                    finalMemberUsernames.toArray(new String[0]));

            System.out.println("Đã tạo nhóm chat mới ID: " + newGroupChatBox.getId() + " từ chat 1-1 ID: "
                    + chatBoxId + " bởi người yêu cầu: " + usernameOfRequestor);
            return newGroupChatBox;

        } else {

            if (!currentChatBox.getCreatedBy().equals(usernameOfRequestor)) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "Chỉ người tạo nhóm mới có quyền thêm thành viên.");
            }

            if (currentChatBox.getMemberAccountUsernames().contains(usernameOfMemberToAdd)) {
                System.out.println("Người dùng " + usernameOfMemberToAdd + " đã là thành viên của nhóm " + chatBoxId);
                return currentChatBox; // Or throw exception
            }

            // Add the new member
            currentChatBox.getMemberAccountUsernames().add(usernameOfMemberToAdd);
            currentChatBox.setUpdatedAt(new Date());
            ChatBox updatedChatBox = chatBoxRepository.save(currentChatBox);

            ChatBoxMember newMemberEntry = ChatBoxMember.builder()
                    .chatBoxId(updatedChatBox.getId())
                    .accountUsername(usernameOfMemberToAdd)
                    .joinedAt(new Date())
                    .build();
            chatBoxMemberRepository.save(newMemberEntry);
            System.out.println(
                    "Đã thêm thành viên " + usernameOfMemberToAdd + " vào nhóm chat ID: " + updatedChatBox.getId()
                            + " bởi người tạo: " + usernameOfRequestor);
            return updatedChatBox;
        }
    }
}
