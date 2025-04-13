package com.husc.lms.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentMessage {
	
    private String lessonId;
    private String courseId;
    private String accountId;
    private String detail;
    private OffsetDateTime createdDate;
}
