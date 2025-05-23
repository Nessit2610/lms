package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInChapterOfAccountResponse {
	private String commentId;
	private String accountUsername;
	private String accountFullname;
	private String detail;
	private String avatar;
	private OffsetDateTime createdAt;
	private OffsetDateTime updateAt;
	private int countOfCommentReply;
	private List<CommentReplyOfCommentInChapter> commentReplyOfCommentInChapters;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommentReplyOfCommentInChapter {
		private String commentReplyId;
		private String accountReplyUsername;
		private String accountReplyFullname;
		private String detail;
		private String avatar;
		private OffsetDateTime createdAt;
		private OffsetDateTime updateDate;
	}
}
