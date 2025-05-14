package com.husc.lms.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageSenderResponse {

    private String id;

    private String chatBoxId;

    private String senderAccount;

    private String avatarSenderAccount;

    private String content;

    private OffsetDateTime createdAt;

    private String path;

    private String type;

    private String filename;
}
