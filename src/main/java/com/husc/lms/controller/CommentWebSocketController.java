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
import com.husc.lms.service.CommentWebSocketService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;
	private final CommentWebSocketService webSocketService;

	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public APIResponse<CommentMessageResponse> handleComment(CommentMessage message) {
		try {
			CommentMessageResponse response = commentService.saveCommentWithReadStatusAndNotification(message);
			webSocketService.sendCommentToTopics(response);
			return APIResponse.<CommentMessageResponse>builder()
					.result(response)
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
			CommentUpdateMessageResponse response = commentService.updateComment(message);
			webSocketService.sendCommentUpdateToTopics(response);
			return APIResponse.<CommentUpdateMessageResponse>builder()
					.result(response)
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
			Boolean response = commentService.deleteComment(message);
			webSocketService.sendCommentDeleteToTopics(response);
			return APIResponse.<Boolean>builder()
					.result(response)
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
