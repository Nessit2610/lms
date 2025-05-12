package com.husc.lms.mongoEntity;

import java.time.OffsetDateTime;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "lms_chat_box_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxMember {

    @Id
    private String id;

    private String chatBoxId;

    private String accountUsername;

    private OffsetDateTime joinedAt;
}
