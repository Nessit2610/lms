package com.husc.lms.mongoEntity;

// import java.time.LocalDateTime; // No longer using LocalDateTime
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lms_chat_boxes") // Collection name was lms_chat_boxes from previous user log
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBox {

    @Id
    private String id;

    private boolean isGroup; // true: group chat, false: 1-1

    private Date createdAt; // Changed to Date

    private String createdBy; // accountId người tạo (group chat)

    private String name; // optional: tên hiển thị phòng

    private List<String> memberAccountUsernames;

    private Date updatedAt; // Changed to Date

    private String lastMessage;

    private Date lastMessageAt;

    private String lastMessageBy;
}