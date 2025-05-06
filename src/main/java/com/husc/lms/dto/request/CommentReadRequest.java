package com.husc.lms.dto.request;

import java.util.List;

import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReadRequest {
	private List<Comment> comments;
    private List<CommentReply> commentReplies;
}
