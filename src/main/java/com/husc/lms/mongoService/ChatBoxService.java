package com.husc.lms.mongoService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.husc.lms.dto.response.ChatBoxResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;

public interface ChatBoxService {
    ChatBox createOrGetOneToOneChatBox(String currentUsername, String anotherUsername);

    ChatBox createGroupChatBox(String name, String creatorUsername, String... memberUsernames);

    Page<ChatBox> getOneToOneChatBoxesForAccount(String accountUsername, Pageable pageable);

    /**
     * Lấy tất cả chatbox mà currentAccount tham gia (bao gồm cả chat 1-1 và group
     * chat)
     * 
     * @param pageNumber Số trang bắt đầu từ 0
     * @param pageSize   Kích thước của trang
     * @return Page chứa danh sách chatbox response
     */
    Page<ChatBoxResponse> getAllChatBoxesForCurrentAccount(int pageNumber, int pageSize);

    Optional<ChatBox> getChatBoxById(String chatBoxId);

    List<ChatBoxMember> getChatBoxMembers(String chatBoxId);

    /**
     * Đổi tên chatbox
     * 
     * @param chatBoxId ID của chatbox
     * @param newName   Tên mới của chatbox
     * @return ChatBox đã được cập nhật
     * @throws AppException nếu không tìm thấy chatbox hoặc người dùng không có
     *                      quyền
     */
    ChatBox renameChatBox(String chatBoxId, String newName);
}
