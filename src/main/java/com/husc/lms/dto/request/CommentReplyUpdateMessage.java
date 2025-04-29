package com.husc.lms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReplyUpdateMessage {
	private String commentReplyId;
	private String usernameReply;
	private String newDetail;
}
