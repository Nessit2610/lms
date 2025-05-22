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
public class CommentUpdateMessageResponse {
    private String chapterId;
    private String courseId;
    private String postId;
    private String commentId;
    private String usernameOwner;
    private String fullnameOwner;
    private String avatarOwner;
    private String newDetail;
    private int countOfReply;
    private OffsetDateTime updateDate;
    private List<CommentReplyResponse> commentReplyResponses;
}
