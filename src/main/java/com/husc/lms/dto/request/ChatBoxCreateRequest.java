package com.husc.lms.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxCreateRequest {
	private List<String> anotherAccounts;
	private String groupName; // Tên cho group chat, có thể null nếu là chat 1-1
}
