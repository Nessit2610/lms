package com.husc.lms.mongoEntity;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lms_chat_boxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBox {

    @Id
    private String id;

    private boolean isGroup;  // true: group chat, false: 1-1
    
    private Date createdAt;

    private String createdBy; // accountId người tạo (group chat)

    private String name; // optional: tên hiển thị phòng
}