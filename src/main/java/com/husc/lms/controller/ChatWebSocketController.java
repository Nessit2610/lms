package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatWebSocketService chatWebSocketService;
	private final ChatBoxMemberService chatBoxMemberService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@MessageMapping("/chat/create")
	public void handleCreateChatBox(@Payload ChatBoxCreateRequest request) {
		try {
			System.out.println("[DEBUG] Received /chat.create request: " + objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println("[DEBUG] Error serializing /chat.create request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Received /chat.create request (raw): " + request.toString());
		}

		ChatBoxCreateResponse response = chatWebSocketService.handleChatCreation(request);

		if (response != null && response.getChatBoxId() != null) {
			try {
				System.out.println("[DEBUG] Sending ChatBoxCreateResponse for /topic/chatbox/" + response.getChatBoxId()
						+ "/created: " + objectMapper.writeValueAsString(response));
			} catch (JsonProcessingException e) {
				System.err.println("[DEBUG] Error serializing ChatBoxCreateResponse to JSON: " + e.getMessage());
			}
			messagingTemplate.convertAndSend("/topic/chatbox/" + response.getChatBoxId() + "/created", response);

			response.getListMemmber().forEach(member -> {
				System.out.println("[DEBUG] Sending ChatBoxCreateResponse notification to user: "
						+ member.getMemberAccountUsername());
				messagingTemplate.convertAndSendToUser(member.getMemberAccountUsername(), "/queue/notifications",
						response);
			});
		} else {
			System.out.println(
					"[DEBUG] ChatBoxCreateResponse is null or ChatBoxId is null after handleChatCreation. No message sent.");
		}
	}

	@MessageMapping("/chat/sendMessage")
	public void handleSendMessage(@Payload ChatMessageSenderRequest request) {
		try {
			System.out
					.println("[DEBUG] Received /chat/sendMessage request: " + objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println("[DEBUG] Error serializing /chat/sendMessage request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Received /chat/sendMessage request (raw): " + request.toString());
		}

		ChatMessageSenderResponse response = chatWebSocketService.handleSendMessage(request);

		if (response != null && response.getChatBoxId() != null) {
			try {
				System.out.println("[DEBUG] Sending ChatMessageSenderResponse to /topic/chatbox/"
						+ response.getChatBoxId() + ": " + objectMapper.writeValueAsString(response));
			} catch (JsonProcessingException e) {
				System.err.println("[DEBUG] Error serializing ChatMessageSenderResponse to JSON: " + e.getMessage());
			}
			messagingTemplate.convertAndSend("/topic/chatbox/" + response.getChatBoxId(), response);
		} else {
			System.out.println(
					"[DEBUG] ChatMessageSenderResponse is null or ChatBoxId is null after handleSendMessage. No message sent.");
		}
	}

	@MessageMapping("/chat/addMembers")
	public void handleAddMembers(@Payload ChatBoxAddMemberRequest request) {
		try {
			System.out
					.println("[DEBUG] Received /chat/addMembers request: " + objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println("[DEBUG] Error serializing /chat/addMembers request to JSON: " + e.getMessage());
			System.out.println("[DEBUG] Received /chat/addMembers request (raw): " + request.toString());
		}

		ChatBoxAddMemberResponse response = chatWebSocketService.handleAddMembersToChatbox(request);

		if (response != null && response.getChatboxId() != null) {
			try {
				System.out.println("[DEBUG] Sending ChatBoxAddMemberResponse to /topic/chatbox/"
						+ response.getChatboxId() + "/membersAdded: " + objectMapper.writeValueAsString(response));
			} catch (JsonProcessingException e) {
				System.err.println("[DEBUG] Error serializing ChatBoxAddMemberResponse to JSON: " + e.getMessage());
			}
			messagingTemplate.convertAndSend("/topic/chatbox/" + response.getChatboxId() + "/membersAdded", response);

			response.getChatMemberReponses().forEach(member -> {
				System.out.println(
						"[DEBUG] Sending ChatBoxAddMemberResponse notification to user: " + member.getMemberAccount());
				messagingTemplate.convertAndSendToUser(member.getMemberAccount(), "/queue/notifications",
						response);
			});

		} else {
			System.out.println(
					"[DEBUG] ChatBoxAddMemberResponse is null or ChatBoxId is null after handleAddMembersToChatbox. No message sent.");
		}
	}
}
