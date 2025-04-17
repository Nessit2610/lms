package com.husc.lms.dto.request;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
	private String senderId;
    private String receiverId;
    private String content;
}
