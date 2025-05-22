package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.husc.lms.dto.request.CommentMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentMessageResponse {
	private String chapterId;
	private String courseId;
	private String commentId;
	private String postId;
    private String username;
    private String fullname;
    private String avatar;
    private String detail;
    private OffsetDateTime createdDate;
    private List<CommentReplyResponse> commentReplyResponses;
}
