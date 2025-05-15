package com.husc.lms.mongoService;

import com.husc.lms.mongoEntity.ChatBox;

public interface ChatBoxMemberService {
    ChatBox addMemberToChatBox(String chatBoxId, String usernameOfMemberToAdd, String usernameOfRequestor);
}
