package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMemberSearchResponse {
	private String accountId;
	private String accountFullname;
	private String accountUsername;
	private String avatar;
}
