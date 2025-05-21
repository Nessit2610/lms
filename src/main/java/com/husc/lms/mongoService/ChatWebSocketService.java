package com.husc.lms.mongoService;

import com.husc.lms.dto.request.ChatBoxAddMemberRequest;
import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxAddMemberResponse;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;

public interface ChatWebSocketService {

    ChatBoxCreateResponse handleChatCreation(ChatBoxCreateRequest request);

    ChatMessageSenderResponse handleSendMessage(ChatMessageSenderRequest chatMessageSenderRequest);

    ChatBoxAddMemberResponse handleAddMembersToChatbox(ChatBoxAddMemberRequest chatBoxAddMemberRequest);
}