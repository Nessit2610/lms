package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentReplyMessage;
import com.husc.lms.dto.request.CommentReplyUpdateMessage;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentReplyResponse;
import com.husc.lms.dto.response.CommentReplyUpdateMessageResponse;
import com.husc.lms.service.CommentReplyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentReplyWebSocketController {
	private final CommentReplyService commentReplyService;

	@MessageMapping("/comment-reply")
	@SendTo("/topic/comment-replies")
	public APIResponse<CommentReplyResponse> handleCommentReply(CommentReplyMessage message) {
		try {
			return APIResponse.<CommentReplyResponse>builder()
					.result(commentReplyService.saveCommentReplyWithReadStatusAndNotification(message))
					.build();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/comment-reply/update")
	@SendTo("/topic/comment-replies")
	public APIResponse<CommentReplyUpdateMessageResponse> updateCommentReply(CommentReplyUpdateMessage message) {
		try {
			return APIResponse.<CommentReplyUpdateMessageResponse>builder()
					.result(commentReplyService.updateCommentReply(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/comment-reply/delete")
	@SendTo("/topic/comment-replies")
	public APIResponse<CommentReplyResponse> deleteComment(CommentReplyUpdateMessage message) {
		try {
			return APIResponse.<CommentReplyResponse>builder()
					.result(commentReplyService.deleteCommentReply(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment-reply")
	@SendTo("/topic/post-comment-replies")
	public APIResponse<CommentReplyResponse> handleCommentReplyPost(CommentReplyMessage message) {
		try {
			return APIResponse.<CommentReplyResponse>builder()
					.result(commentReplyService.saveCommentReplyWithReadStatusAndNotification(message))
					.build();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment-reply/update")
	@SendTo("/topic/post-comment-replies")
	public APIResponse<CommentReplyUpdateMessageResponse> updateCommentReplyPost(CommentReplyUpdateMessage message) {
		try {
			return APIResponse.<CommentReplyUpdateMessageResponse>builder()
					.result(commentReplyService.updateCommentReply(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@MessageMapping("/post-comment-reply/delete")
	@SendTo("/topic/post-comment-replies")
	public APIResponse<CommentReplyResponse> deleteCommentPost(CommentReplyUpdateMessage message) {
		try {
			return APIResponse.<CommentReplyResponse>builder()
					.result(commentReplyService.deleteCommentReply(message))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
