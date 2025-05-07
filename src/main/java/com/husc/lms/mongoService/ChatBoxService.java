package com.husc.lms.mongoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.husc.lms.mongoEntity.ChatBox;

public interface ChatBoxService {
    ChatBox createOrGetOneToOneChatBox(String accountId1, String accountId2);

    ChatBox createGroupChatBox(String name, String createdBy, String... accountIds);

    Page<ChatBox> getOneToOneChatBoxesForAccount(Pageable pageable);
}
