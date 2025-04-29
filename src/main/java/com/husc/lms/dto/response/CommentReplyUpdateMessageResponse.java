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
public class CommentReplyUpdateMessageResponse {
	private String parentCommentId;	
	private String usernameOwner;
	private String fullnameOwner;
	private String commentReplyId;
    private String usernameReply;
    private String fullnameReply;
    private String avatarReply;
    private String newDetail;
    private OffsetDateTime updateDate;
}
