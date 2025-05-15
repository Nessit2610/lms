package com.husc.lms.mongoService;

import com.husc.lms.dto.request.ChatAddMemberRequest;
import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;
import com.husc.lms.mongoEntity.ChatBox;

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

    /**
     * Xử lý yêu cầu thêm thành viên vào chat box.
     * 
     * @param request Đối tượng chứa thông tin để thêm thành viên.
     * @return ChatBox đã được cập nhật hoặc ChatBox mới (nếu từ 1-1 tạo thành
     *         group).
     */
    ChatBox handleAddMemberRequest(ChatAddMemberRequest request);
}