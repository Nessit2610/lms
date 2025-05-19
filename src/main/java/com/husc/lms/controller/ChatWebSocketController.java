package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;
import com.husc.lms.mongoService.ChatBoxMemberService;
import com.husc.lms.mongoService.ChatWebSocketService;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.husc.lms.dto.request.ChatBoxAddMemberRequest;
import com.husc.lms.dto.response.ChatBoxAddMemberResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
	private final ChatWebSocketService chatWebSocketService;
	private final ChatBoxMemberService chatBoxMemberService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@MessageMapping("/chat/create")
	public void handleCreateChatBox(@Payload ChatBoxCreateRequest request) {
		try {
			System.out.println(
					"[DEBUG] Controller: Received /chat/create request: " + objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println("[DEBUG] Controller: Error serializing /chat.create request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Controller: Received /chat/create request (raw): " + request.toString());
		}

		chatWebSocketService.handleChatCreation(request);
	}

	@MessageMapping("/chat/sendMessage")
	public void handleSendMessage(@Payload ChatMessageSenderRequest request) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		System.out.println("[DEBUG] Authorities: " + auth.getAuthorities());
		try {
			System.out
					.println("[DEBUG] Controller: Received /chat/sendMessage request: "
							+ objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println(
					"[DEBUG] Controller: Error serializing /chat/sendMessage request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Controller: Received /chat/sendMessage request (raw): " + request.toString());
		}

		chatWebSocketService.handleSendMessage(request);
	}

	@MessageMapping("/chat/addMembers")
	public void handleAddMembers(@Payload ChatBoxAddMemberRequest request) {
		try {
			System.out
					.println("[DEBUG] Controller: Received /chat/addMembers request: "
							+ objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println(
					"[DEBUG] Controller: Error serializing /chat/addMembers request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Controller: Received /chat/addMembers request (raw): " + request.toString());
		}

		chatWebSocketService.handleAddMembersToChatbox(request);
	}
}
