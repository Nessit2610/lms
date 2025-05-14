package com.husc.lms.mongoService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable Thông tin phân trang
     * @return Page chứa danh sách chatbox
     */
    Page<ChatBox> getAllChatBoxesForCurrentAccount(Pageable pageable);

    Optional<ChatBox> getChatBoxById(String chatBoxId);

    List<ChatBoxMember> getChatBoxMembers(String chatBoxId);
}
