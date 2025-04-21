package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;
	
	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public CommentMessage handleComment(CommentMessage message) {
	    try {
	        commentService.handleWebSocketComment(message);
	        return message;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


}
