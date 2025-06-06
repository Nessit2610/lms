package com.husc.lms.dto.request;

import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageSenderRequest {
	private String senderAccount;
	private String chatBoxId;
	private String content;
	private MultipartFile file;
	private String fileType;
}
