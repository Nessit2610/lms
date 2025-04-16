package com.husc.lms.mongoEntity;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;

    private String chatBoxId;

    private String senderId; // chính là account.getId() bên MySQL

    private String content;

    private Date createdAt;

    private boolean read;
}
