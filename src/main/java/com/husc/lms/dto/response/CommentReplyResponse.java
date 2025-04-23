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
public class CommentReplyResponse {
	private String commentReplyId;
    private String username;
    private String avatar;
    private String detail;
    private OffsetDateTime createdDate;
}