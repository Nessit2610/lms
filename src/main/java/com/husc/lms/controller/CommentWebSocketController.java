package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.request.CommentReplyMessage;
import com.husc.lms.service.CommentReplyService;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;
	private final CommentReplyService commentReplyService;

	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public CommentMessage handleComment(CommentMessage message) {
	    try {
//	        commentService.handleWebSocketComment(message);
	        commentService.saveCommentWithReadStatusAndNotification(message);
	        return message;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	@MessageMapping("/comment-reply")
	@SendTo("/topic/comment-replies")
	public CommentReplyMessage handleCommentReply(CommentReplyMessage message) {
	    try {
	        commentReplyService.saveCommentReplyWithReadStatusAndNotification(message);
	        return message;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
