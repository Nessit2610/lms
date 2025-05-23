package com.husc.lms.mongoService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.response.ChatBoxResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatBoxMember;

public interface ChatBoxService {
    ChatBox createOrGetOneToOneChatBox(String currentUsername, String anotherUsername);

    ChatBox createGroupChatBox(String name, String creatorUsername, String... memberUsernames);

    Page<ChatBox> getOneToOneChatBoxesForAccount(String accountUsername, Pageable pageable);

    Page<ChatBoxResponse> getAllChatBoxesForCurrentAccount(int pageNumber, int pageSize);

    Optional<ChatBox> getChatBoxById(String chatBoxId);

    List<ChatBoxMember> getChatBoxMembers(String chatBoxId);

    ChatBox renameChatBox(String chatBoxId, String newName);

    Page<ChatBoxResponse> searchByNameOfChatBox(String nameRegex, int pageNumber, int pageSize);

    String uploadAvatarChatBox(String chatBoxId, MultipartFile file);

}
