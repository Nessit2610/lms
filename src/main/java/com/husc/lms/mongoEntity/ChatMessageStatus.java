package com.husc.lms.mongoEntity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lms_chat_message_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageStatus {

    @Id
    private String id;

    private String messageId; // ID của tin nhắn
    private String chatBoxId; // ID của chatbox
    private String accountUsername; // ID của người dùng
    private boolean isRead; // Trạng thái đã đọc
    private Date readAt; // Thời gian đọc tin nhắn
}