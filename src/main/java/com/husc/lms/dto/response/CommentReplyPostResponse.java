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
public class CommentReplyPostResponse {
	private String commentId;
    private String commentReplyId;
    private String usernameOwner;
    private String fullnameOwner;
    private String usernameReply;
    private String fullnameReply;
    private String avatarReply;
    private String detail;
    private OffsetDateTime createdDate;
    private OffsetDateTime updateDate;
    private Integer replyCount;
}
