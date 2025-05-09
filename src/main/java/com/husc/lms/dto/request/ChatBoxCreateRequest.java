package com.husc.lms.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatBoxCreateRequest {
	private List<String> anotherAccounts;
	private String groupName; // Tên cho group chat, có thể null nếu là chat 1-1
	private String currentAccountUsername;
}
