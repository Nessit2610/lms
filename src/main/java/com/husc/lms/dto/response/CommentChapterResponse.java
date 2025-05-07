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
public class CommentChapterResponse {
	private String commentId;
	private String username;
	private String fullname;
	private String avatar;
	private String detail;
	private OffsetDateTime createdDate;
	private int countOfReply;

	public CommentChapterResponse(String commentId, String username, String detail, OffsetDateTime createdDate) {
		super();
		this.commentId = commentId;
		this.username = username;
		this.detail = detail;
		this.createdDate = createdDate;
	}
}
