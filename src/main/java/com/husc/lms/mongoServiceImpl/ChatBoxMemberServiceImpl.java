package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.husc.lms.dto.response.ChatMemberSearchResponse;
import com.husc.lms.entity.Account;
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
                        // This is a 1-on-1 chat. We are converting it to a new group chat.
                        // The new group should contain the original two members +
                        // usernameOfMemberToAdd.

                        List<String> newGroupMemberUsernames = new ArrayList<>();

                        // Add the person requesting the change (one of the original 1-on-1 members)
                        newGroupMemberUsernames.add(usernameOfRequestor);

                        // Add the new member to be added
                        if (!newGroupMemberUsernames.contains(usernameOfMemberToAdd)) {
                                newGroupMemberUsernames.add(usernameOfMemberToAdd);
                        }

                        // Find all members of the original 1-on-1 chat using ChatBoxMemberRepository
                        // This is more reliable than currentChatBox.getMemberAccountUsernames() if that
                        // field
                        // is not consistently populated for 1-on-1 chats.
                        List<ChatBoxMember> originalChatMembers = chatBoxMemberRepository
                                        .findByChatBoxId(currentChatBox.getId());
                        for (ChatBoxMember member : originalChatMembers) {
                                if (!newGroupMemberUsernames.contains(member.getAccountUsername())) {
                                        newGroupMemberUsernames.add(member.getAccountUsername());
                                }
                        }

                        // Ensure distinctness, though the above logic tries to maintain it.
                        List<String> finalMemberUsernamesForNewGroup = newGroupMemberUsernames.stream().distinct()
                                        .collect(Collectors.toList());

                        if (finalMemberUsernamesForNewGroup.size() < 3) {
                                // This can still happen if, e.g., usernameOfMemberToAdd was one of the original
                                // 1-on-1 members,
                                // or if the original 1-on-1 chat somehow had fewer than 2 distinct members via
                                // chatBoxMemberRepository.
                                System.err.println("[DEBUG] Failed to form a group of at least 3. Requestor: "
                                                + usernameOfRequestor +
                                                ", MemberToAdd: " + usernameOfMemberToAdd +
                                                ", Original Chat Members (from repo): "
                                                + originalChatMembers.stream().map(ChatBoxMember::getAccountUsername)
                                                                .collect(Collectors.toList())
                                                +
                                                ", Final list for new group: " + finalMemberUsernamesForNewGroup);
                                throw new AppException(ErrorCode.INVALID_OPERATION,
                                                "Không thể tạo nhóm với ít hơn 3 thành viên từ chat 1-1. Số lượng thành viên cuối cùng: "
                                                                + finalMemberUsernamesForNewGroup.size()
                                                                + ". Danh sách: "
                                                                + finalMemberUsernamesForNewGroup);
                        }
                        String newGroupName = "Nhóm: "
                                        + finalMemberUsernamesForNewGroup.stream().limit(3)
                                                        .collect(Collectors.joining(", "));

                        ChatBox newGroupChatBox = chatBoxService.createGroupChatBox(newGroupName, usernameOfRequestor,
                                        finalMemberUsernamesForNewGroup.toArray(new String[0]));

                        System.out.println("Đã tạo nhóm chat mới ID: " + newGroupChatBox.getId() + " từ chat 1-1 ID: "
                                        + chatBoxId + " bởi người yêu cầu: " + usernameOfRequestor);
                        return newGroupChatBox;

                } else {

                        if (!currentChatBox.getCreatedBy().equals(usernameOfRequestor)) {
                                throw new AppException(ErrorCode.UNAUTHORIZED,
                                                "Chỉ người tạo nhóm mới có quyền thêm thành viên.");
                        }

                        if (currentChatBox.getMemberAccountUsernames().contains(usernameOfMemberToAdd)) {
                                System.out.println("Người dùng " + usernameOfMemberToAdd + " đã là thành viên của nhóm "
                                                + chatBoxId);
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
                                        "Đã thêm thành viên " + usernameOfMemberToAdd + " vào nhóm chat ID: "
                                                        + updatedChatBox.getId()
                                                        + " bởi người tạo: " + usernameOfRequestor);
                        return updatedChatBox;
                }
        }

        @Override
        public List<ChatBoxMember> getChatBoxMembersByChatBoxId(String chatBoxId) {
                if (chatBoxId == null || chatBoxId.trim().isEmpty()) {
                        throw new AppException(ErrorCode.INVALID_PARAMETER, "ChatBox ID không được để trống.");
                }
                return chatBoxMemberRepository.findByChatBoxId(chatBoxId);
        }

        @Override
        @Transactional
        public void removeMemberFromChatBox(String chatBoxId, String usernameOfMemberToRemove,
                        String usernameOfRequestor) {
                // Validate accounts
                accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfRequestor)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Tài khoản yêu cầu (requestor) không tìm thấy: "
                                                                + usernameOfRequestor));
                accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfMemberToRemove)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Tài khoản cần xoá (member to remove) không tìm thấy: "
                                                                + usernameOfMemberToRemove));

                // Fetch ChatBox
                ChatBox chatBox = chatBoxRepository.findById(chatBoxId)
                                .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND,
                                                "ChatBox không tìm thấy với ID: " + chatBoxId));

                // Authorization: Only the creator of the group can remove members
                if (!chatBox.getCreatedBy().equals(usernameOfRequestor)) {
                        throw new AppException(ErrorCode.UNAUTHORIZED,
                                        "Chỉ người tạo nhóm mới có quyền xoá thành viên.");
                }

                // Cannot remove from a 1-on-1 chat this way (it's not a group management
                // operation)
                if (!chatBox.isGroup()) {
                        throw new AppException(ErrorCode.INVALID_OPERATION,
                                        "Không thể xoá thành viên từ một cuộc trò chuyện 1-1 bằng chức năng này.");
                }

                // Prevent creator from removing themselves
                if (usernameOfMemberToRemove.equals(chatBox.getCreatedBy())) {
                        throw new AppException(ErrorCode.INVALID_OPERATION,
                                        "Người tạo nhóm không thể tự xoá chính mình khỏi nhóm.");
                }

                // Check if the member to remove is actually in the chatBox's member list
                if (!chatBox.getMemberAccountUsernames().contains(usernameOfMemberToRemove)) {
                        throw new AppException(ErrorCode.MEMBER_NOT_FOUND_IN_CHATBOX,
                                        "Thành viên " + usernameOfMemberToRemove + " không có trong nhóm chat "
                                                        + chatBoxId);
                }

                // Also check using ChatBoxMemberRepository for consistency, though the above
                // check is primary for the list in ChatBox
                if (!chatBoxMemberRepository.existsByChatBoxIdAndAccountUsername(chatBoxId, usernameOfMemberToRemove)) {
                        // This case should ideally not happen if ChatBox.memberAccountUsernames is kept
                        // in sync
                        System.err.println("[WARN] Member " + usernameOfMemberToRemove
                                        + " was in ChatBox.memberAccountUsernames but not found in ChatBoxMemberRepository for chatBoxId "
                                        + chatBoxId + ". Proceeding with removal based on ChatBox list.");
                }

                // Perform deletion from ChatBoxMember collection
                chatBoxMemberRepository.deleteByChatBoxIdAndAccountUsername(chatBoxId, usernameOfMemberToRemove);

                // Update ChatBox entity: remove member from the list and update timestamp
                boolean removed = chatBox.getMemberAccountUsernames().remove(usernameOfMemberToRemove);
                if (removed) {
                        chatBox.setUpdatedAt(new Date());
                        chatBoxRepository.save(chatBox);
                        System.out.println("Đã xoá thành viên " + usernameOfMemberToRemove + " khỏi nhóm chat ID: "
                                        + chatBoxId
                                        + " bởi người tạo: " + usernameOfRequestor);
                } else {
                        // This should ideally not be reached if the .contains check above passed and no
                        // concurrent modification occurred
                        System.err.println("[WARN] Thành viên " + usernameOfMemberToRemove
                                        + " không thể xoá khỏi danh sách memberAccountUsernames của ChatBox ID: "
                                        + chatBoxId
                                        + " dù đã tồn tại trước đó.");
                        // Consider if an exception should be thrown here if strict consistency is
                        // required
                }
        }

        @Override
        public List<ChatMemberSearchResponse> findAccountsNotInChatBox(String chatBoxId, String searchString) {
                // 1. Get members already in the chat box
                List<ChatBoxMember> membersInChatBox = chatBoxMemberRepository.findByChatBoxId(chatBoxId);

                // 2. Extract their usernames
                List<String> excludedUsernames = membersInChatBox.stream()
                                .map(ChatBoxMember::getAccountUsername)
                                .collect(Collectors.toList());

                // 3. Find accounts matching the search term but not in the excluded list
                List<Account> accounts = accountRepository.findBySearchTermAndUsernameNotInAndDeletedDateIsNull(
                                searchString,
                                excludedUsernames);

                // 4. Map List<Account> to List<ChatMemberSearchResponse>
                List<ChatMemberSearchResponse> responseList = new ArrayList<>();
                for (Account account : accounts) {
                        String fullname = account.getStudent() != null ? account.getStudent().getFullName()
                        				: account.getTeacher() != null ? account.getTeacher().getFullName()
                        				: "";
                        String avatar = account.getStudent() != null ? account.getStudent().getAvatar()
		                				: account.getTeacher() != null ? account.getTeacher().getAvatar()
		                				: "";;

                        

                        responseList.add(ChatMemberSearchResponse.builder()
                                        .accountId(account.getId())
                                        .accountFullname(fullname)
                                        .avatar(avatar)
                                        .build());
                }
                return responseList;
        }

}
