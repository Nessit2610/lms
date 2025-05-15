package com.husc.lms.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.ChatAddMemberRequest;
import com.husc.lms.dto.request.ChatBoxCreateRequest;
import com.husc.lms.dto.request.ChatMessageSenderRequest;
import com.husc.lms.dto.response.ChatBoxCreateResponse;
import com.husc.lms.dto.response.ChatMessageSenderResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoService.ChatWebSocketService;
import com.husc.lms.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.security.Principal;

// @Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/lms/chat")
public class ChatWebSocketController {
	private final SimpMessagingTemplate messagingTemplate;
	private final ChatWebSocketService chatWebSocketService;
	private final AccountRepository accountRepository;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@MessageMapping("/chat/create")
	public void handleCreateChatBox(@Payload ChatBoxCreateRequest request, Principal principal) {
		if (principal == null || principal.getName() == null) {
			System.err.println("[DEBUG] /chat/create - Principal is null or has no name. Unauthorized attempt?");
			return;
		}
		request.setCurrentAccountUsername(principal.getName());

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

	@MessageMapping("/chat/addMember")
	public void handleAddMemberToChatBox(@Payload ChatAddMemberRequest request, Principal principal) {
		if (principal == null || principal.getName() == null) {
			System.err.println("[DEBUG] /chat/addMember - Principal is null or has no name. Unauthorized attempt?");
			return;
		}
		request.setUsernameOfRequestor(principal.getName());

		try {
			System.out.println("[DEBUG] Received /chat/addMember request: " + objectMapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			System.err.println("[DEBUG] Error serializing /chat/addMember request: " + e.getMessage());
		}

		ChatBox resultChatBox = chatWebSocketService.handleAddMemberRequest(request);

		if (resultChatBox != null) {
			try {
				String resultJson = objectMapper.writeValueAsString(resultChatBox);
				System.out.println("[DEBUG] handleAddMemberToChatBox result: " + resultJson);

				boolean newGroupCreatedFromOneOnOne = !resultChatBox.getId().equals(request.getChatBoxId())
						&& resultChatBox.isGroup();

				if (newGroupCreatedFromOneOnOne) {
					messagingTemplate.convertAndSend("/topic/chatbox/" + resultChatBox.getId() + "/created",
							resultChatBox);
					System.out.println(
							"[DEBUG] Sent new group creation to /topic/chatbox/" + resultChatBox.getId() + "/created");

					resultChatBox.getMemberAccountUsernames().forEach(memberUsername -> {
						ChatBoxCreateResponse notificationPayload = ChatBoxCreateResponse.fromChatBox(resultChatBox,
								memberUsername, accountRepository);
						messagingTemplate.convertAndSendToUser(memberUsername, "/queue/notifications",
								notificationPayload);
						System.out.println("[DEBUG] Sent new group notification to user queue: " + memberUsername);
					});
				} else {
					messagingTemplate.convertAndSend("/topic/chatbox/" + resultChatBox.getId() + "/updated",
							resultChatBox);
					System.out.println(
							"[DEBUG] Sent group update to /topic/chatbox/" + resultChatBox.getId() + "/updated");

					ChatBoxCreateResponse addedMemberNotification = ChatBoxCreateResponse.fromChatBox(resultChatBox,
							request.getUsernameOfMemberToAdd(), accountRepository);
					messagingTemplate.convertAndSendToUser(request.getUsernameOfMemberToAdd(), "/queue/notifications",
							addedMemberNotification);
					System.out.println("[DEBUG] Sent added to group notification to user queue: "
							+ request.getUsernameOfMemberToAdd());

					resultChatBox.getMemberAccountUsernames().stream()
							.filter(username -> !username.equals(request.getUsernameOfMemberToAdd()))
							.forEach(memberUsername -> {
								ChatBoxCreateResponse existingMemberUpdate = ChatBoxCreateResponse
										.fromChatBox(resultChatBox, memberUsername, accountRepository);
								messagingTemplate.convertAndSendToUser(memberUsername, "/queue/notifications",
										existingMemberUpdate);
								System.out.println(
										"[DEBUG] Sent group update notification to existing member: " + memberUsername);
							});
				}

			} catch (JsonProcessingException e) {
				System.err.println("[DEBUG] Error serializing resultChatBox for /chat/addMember: " + e.getMessage());
			}
		} else {
			System.out.println("[DEBUG] handleAddMemberRequest returned null. No WebSocket messages sent.");
		}
	}

	// @PostMapping("/sendMessage")
	// public ChatMessageSenderResponse sendFileMessage(
	// @RequestParam("chatBoxId") String chatBoxId,
	// @RequestParam("senderAccount") String senderAccount,
	// @RequestParam(value = "content", required = false) String content,
	// @RequestParam("file") MultipartFile file,
	// @RequestParam("fileType") String fileType) {
	//
	// ChatMessageSenderRequest request = ChatMessageSenderRequest.builder()
	// .chatBoxId(chatBoxId)
	// .senderAccount(senderAccount)
	// .content(content)
	// .file(file)
	// .fileType(fileType)
	// .build();
	//
	// ChatMessageSenderResponse response =
	// chatWebSocketService.handleSendMessage(request);
	//
	// if (response != null && response.getChatBoxId() != null) {
	// messagingTemplate.convertAndSend("/topic/chatbox/" + response.getChatBoxId(),
	// response);
	// }
	// return response;
	// }
}
