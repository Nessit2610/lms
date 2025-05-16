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
public class ChatBoxAddMemberRequest {
	private String chatboxId;
	private String chatBoxName;
	private List<ChatMemberRequest> chatMemberRequests;
	private String usernameOfRequestor;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChatMemberRequest {
		private String memberId;
		private String memberAccount;
	}
}
