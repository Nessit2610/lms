package com.husc.lms.dto.response;

import java.util.Date;
import java.util.List;

import com.husc.lms.dto.request.ChatBoxCreateRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatBoxAddMemberResponse {
	private String chatboxId;
	private String chatboxName;
	private List<ChatMemberResponse> chatMemberReponses;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ChatMemberResponse {
		private String chatMemberId;
		private String memberName;
		private String memberAccount;
		private String memberAvatar;
		private Date joinAt;
	}
}
