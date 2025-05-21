package com.husc.lms.mongoService;

import com.husc.lms.dto.request.ChatBoxAddMemberRequest;
import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxAddMemberResponse;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;

public interface ChatWebSocketService {
    /**
     * Tạo hoặc lấy chatbox 1-1 hoặc tạo group chat.
     * 
     * @param request Đối tượng chứa thông tin để tạo chatbox
     * @return ChatBox đã tồn tại hoặc mới tạo
     */
    ChatBoxCreateResponse handleChatCreation(ChatBoxCreateRequest request);

    /**
     * Xử lý gửi tin nhắn trong chatbox
     * 
     * @param chatMessageSenderRequest ID của chatbox
     * @return Tin nhắn đã gửi
     */
    ChatMessageSenderResponse handleSendMessage(ChatMessageSenderRequest chatMessageSenderRequest);

    ChatBoxAddMemberResponse handleAddMembersToChatbox(ChatBoxAddMemberRequest chatBoxAddMemberRequest);
}