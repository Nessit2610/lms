package com.husc.lms.mongoServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.husc.lms.dto.response.ChatBoxMemberResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;

import com.husc.lms.service.OffsetLimitPageRequest;

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
                        List<String> newGroupMemberUsernames = new ArrayList<>();

                        newGroupMemberUsernames.add(usernameOfRequestor);

                        if (!newGroupMemberUsernames.contains(usernameOfMemberToAdd)) {
                                newGroupMemberUsernames.add(usernameOfMemberToAdd);
                        }

                        List<ChatBoxMember> originalChatMembers = chatBoxMemberRepository
                                        .findByChatBoxId(currentChatBox.getId());
                        for (ChatBoxMember member : originalChatMembers) {
                                if (!newGroupMemberUsernames.contains(member.getAccountUsername())) {
                                        newGroupMemberUsernames.add(member.getAccountUsername());
                                }
                        }

                        List<String> finalMemberUsernamesForNewGroup = newGroupMemberUsernames.stream().distinct()
                                        .collect(Collectors.toList());

                        if (finalMemberUsernamesForNewGroup.size() < 3) {
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
                                return currentChatBox;
                        }

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

        // @Override
        // public List<ChatBoxMember> getChatBoxMembersByChatBoxId(String chatBoxId) {
        // if (chatBoxId == null || chatBoxId.trim().isEmpty()) {
        // throw new AppException(ErrorCode.INVALID_PARAMETER, "ChatBox ID không được để
        // trống.");
        // }
        // return chatBoxMemberRepository.findByChatBoxId(chatBoxId);
        // }
        @Override
        public List<ChatBoxMemberResponse> getChatBoxMembersByChatBoxId(String chatBoxId) {
                if (chatBoxId == null || chatBoxId.trim().isEmpty()) {
                        throw new AppException(ErrorCode.INVALID_PARAMETER, "ChatBox ID không được để trống.");
                }

                List<ChatBoxMember> chatBoxMembers = chatBoxMemberRepository.findByChatBoxId(chatBoxId);

                List<ChatBoxMemberResponse> response = chatBoxMembers.stream()
                                .map(member -> {
                                        Account account = accountRepository
                                                        .findByUsernameAndDeletedDateIsNull(member.getAccountUsername())
                                                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                                        "Account not found: "
                                                                                        + member.getAccountUsername()));

                                        String fullName = account.getStudent() != null
                                                        ? account.getStudent().getFullName()
                                                        : (account.getTeacher() != null
                                                                        ? account.getTeacher().getFullName()
                                                                        : account.getUsername());

                                        String avatar = account.getStudent() != null
                                                        ? account.getStudent().getAvatar()
                                                        : (account.getTeacher() != null
                                                                        ? account.getTeacher().getAvatar()
                                                                        : "");

                                        return ChatBoxMemberResponse.builder()
                                                        .id(member.getId())
                                                        .chatBoxId(member.getChatBoxId())
                                                        .accountUsername(member.getAccountUsername())
                                                        .accountFullname(fullName)
                                                        .avatar(avatar)
                                                        .joinedAt(member.getJoinedAt())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return response;
        }

        @Override
        @Transactional
        public void removeMemberFromChatBox(String chatBoxId, String usernameOfMemberToRemove,
                        String usernameOfRequestor) {

                accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfRequestor)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Tài khoản yêu cầu (requestor) không tìm thấy: "
                                                                + usernameOfRequestor));
                accountRepository.findByUsernameAndDeletedDateIsNull(usernameOfMemberToRemove)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND,
                                                "Tài khoản cần xoá (member to remove) không tìm thấy: "
                                                                + usernameOfMemberToRemove));

                ChatBox chatBox = chatBoxRepository.findById(chatBoxId)
                                .orElseThrow(() -> new AppException(ErrorCode.CHATBOX_NOT_FOUND,
                                                "ChatBox không tìm thấy với ID: " + chatBoxId));

                if (!chatBox.getCreatedBy().equals(usernameOfRequestor)) {
                        throw new AppException(ErrorCode.UNAUTHORIZED,
                                        "Chỉ người tạo nhóm mới có quyền xoá thành viên.");
                }

                if (!chatBox.isGroup()) {
                        throw new AppException(ErrorCode.INVALID_OPERATION,
                                        "Không thể xoá thành viên từ một cuộc trò chuyện 1-1 bằng chức năng này.");
                }

                if (usernameOfMemberToRemove.equals(chatBox.getCreatedBy())) {
                        throw new AppException(ErrorCode.INVALID_OPERATION,
                                        "Người tạo nhóm không thể tự xoá chính mình khỏi nhóm.");
                }

                if (!chatBox.getMemberAccountUsernames().contains(usernameOfMemberToRemove)) {
                        throw new AppException(ErrorCode.MEMBER_NOT_FOUND_IN_CHATBOX,
                                        "Thành viên " + usernameOfMemberToRemove + " không có trong nhóm chat "
                                                        + chatBoxId);
                }

                if (!chatBoxMemberRepository.existsByChatBoxIdAndAccountUsername(chatBoxId, usernameOfMemberToRemove)) {

                        System.err.println("[WARN] Member " + usernameOfMemberToRemove
                                        + " was in ChatBox.memberAccountUsernames but not found in ChatBoxMemberRepository for chatBoxId "
                                        + chatBoxId + ". Proceeding with removal based on ChatBox list.");
                }

                chatBoxMemberRepository.deleteByChatBoxIdAndAccountUsername(chatBoxId, usernameOfMemberToRemove);

                boolean removed = chatBox.getMemberAccountUsernames().remove(usernameOfMemberToRemove);
                if (removed) {
                        chatBox.setUpdatedAt(new Date());
                        chatBoxRepository.save(chatBox);
                        System.out.println("Đã xoá thành viên " + usernameOfMemberToRemove + " khỏi nhóm chat ID: "
                                        + chatBoxId
                                        + " bởi người tạo: " + usernameOfRequestor);
                } else {
                        System.err.println("[WARN] Thành viên " + usernameOfMemberToRemove
                                        + " không thể xoá khỏi danh sách memberAccountUsernames của ChatBox ID: "
                                        + chatBoxId
                                        + " dù đã tồn tại trước đó.");
                }
        }

        @Override
        public Page<ChatMemberSearchResponse> findAccountsNotInChatBox(String chatBoxId, String searchString,
                        int pageNumber, int pageSize) {
                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }
                String username = SecurityContextHolder.getContext().getAuthentication().getName();

                List<String> excludedUsernames;
                if (chatBoxId != null) {
                        List<ChatBoxMember> membersInChatBox = chatBoxMemberRepository.findByChatBoxId(chatBoxId);
                        excludedUsernames = membersInChatBox.stream()
                                        .map(ChatBoxMember::getAccountUsername)
                                        .collect(Collectors.toList());
                } else {
                        excludedUsernames = new ArrayList<>();
                        excludedUsernames.add(username);
                }

                int actualOffset = pageNumber;
                int actualLimit = pageSize + 1;

                // Sử dụng OffsetLimitPageRequest giống như searchByNameOfChatBox
                Sort sort = Sort.by(Sort.Direction.ASC, "username");
                OffsetLimitPageRequest fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<Account> fetchedAccountsPage = accountRepository
                                .findBySearchTermAndUsernameNotInAndDeletedDateIsNullWithPagination(
                                                searchString,
                                                excludedUsernames,
                                                fetchPageable);
                List<Account> fetchedContent = fetchedAccountsPage.getContent();

                boolean hasNext = fetchedContent.size() > pageSize;
                List<Account> accountsToReturn = hasNext ? fetchedContent.subList(0, pageSize) : fetchedContent;

                List<ChatMemberSearchResponse> responseList = accountsToReturn.stream()
                                .map(account -> {
                                        String fullname = account.getStudent() != null
                                                        ? account.getStudent().getFullName()
                                                        : account.getTeacher() != null
                                                                        ? account.getTeacher().getFullName()
                                                                        : "";
                                        String avatar = account.getStudent() != null
                                                        ? account.getStudent().getAvatar()
                                                        : account.getTeacher() != null
                                                                        ? account.getTeacher().getAvatar()
                                                                        : "";

                                        return ChatMemberSearchResponse.builder()
                                                        .accountId(account.getId())
                                                        .accountUsername(account.getUsername())
                                                        .accountFullname(fullname)
                                                        .avatar(avatar)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                // Tạo PageRequest giống như searchByNameOfChatBox
                Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);
                long totalElements = fetchedAccountsPage.getTotalElements();
                return new PageImpl<>(responseList, returnPageable, totalElements);
        }

}
