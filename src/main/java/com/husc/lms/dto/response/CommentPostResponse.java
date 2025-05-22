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
public class CommentPostResponse {
	private String commentId;
	private String username;
	private String fullname;
	private String avatar;
	private String detail;
	private OffsetDateTime createdDate;
	private OffsetDateTime updateDate;
	private int countOfReply;
}
