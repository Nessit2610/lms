package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentChapterResponse {
	private String commentId;
    private String username;
    private String detail;
    private OffsetDateTime createdDate;
    private List<CommentReplyResponse> commentReplyResponses;
    
    
	public CommentChapterResponse(String commentId, String username, String detail, OffsetDateTime createdDate) {
		super();
		this.commentId = commentId;
		this.username = username;
		this.detail = detail;
		this.createdDate = createdDate;
	}
    
    
}
