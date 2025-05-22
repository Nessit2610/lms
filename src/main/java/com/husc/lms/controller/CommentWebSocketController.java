package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.request.CommentUpdateMessage;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentMessageResponse;
import com.husc.lms.dto.response.CommentUpdateMessageResponse;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;

	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public APIResponse<CommentMessageResponse> handleComment(CommentMessage message) {
		try {
			return APIResponse.<CommentMessageResponse>builder()
					.result(commentService.saveCommentWithReadStatusAndNotification(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/comment/update")
	@SendTo("/topic/comments")
	public APIResponse<CommentUpdateMessageResponse> updateComment(CommentUpdateMessage message) {
		try {
			return APIResponse.<CommentUpdateMessageResponse>builder()
					.result(commentService.updateComment(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/comment/delete")
	@SendTo("/topic/comments")
	public APIResponse<Boolean> deleteComment(CommentUpdateMessage message) {
		try {
			return APIResponse.<Boolean>builder()
					.result(commentService.deleteComment(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment")
	@SendTo("/topic/post-comments")
	public APIResponse<CommentMessageResponse> handleCommentPost(CommentMessage message) {
		try {
			return APIResponse.<CommentMessageResponse>builder()
					.result(commentService.saveCommentWithReadStatusAndNotification(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment/update")
	@SendTo("/topic/post-comments")
	public APIResponse<CommentUpdateMessageResponse> updateCommentPost(CommentUpdateMessage message) {
		try {
			return APIResponse.<CommentUpdateMessageResponse>builder()
					.result(commentService.updateComment(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment/delete")
	@SendTo("/topic/post-comments")
	public APIResponse<Boolean> deleteCommentPost(CommentUpdateMessage message) {
		try {
			return APIResponse.<Boolean>builder()
					.result(commentService.deleteComment(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
