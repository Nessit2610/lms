package com.husc.lms.dto.request;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentMessage {
	
    private String chapterId;
    private String courseId;
    private String postId;
    private String username;
    private String detail;
    private OffsetDateTime createdDate;
}
