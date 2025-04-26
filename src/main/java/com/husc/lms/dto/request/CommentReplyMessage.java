package com.husc.lms.dto.request;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplyMessage {
    private String ownerUsername;
    private String replyUsername;
    private String chapterId;
    private String courseId;
    private String parentCommentId;
    private String detail;
}