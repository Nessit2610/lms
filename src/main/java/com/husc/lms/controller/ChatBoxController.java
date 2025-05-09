package com.husc.lms.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.mongoService.ChatMessageStatusService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.security.Principal;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatBox")
@RequiredArgsConstructor
public class ChatBoxController {
	private final ChatBoxService chatBoxService;
	private final ChatMessageService chatMessageService;
	private final ChatMessageStatusService chatMessageStatusService;

	@GetMapping("")
	public APIResponse<Page<ChatBox>> getAllChatBoxes(
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize,
				Sort.by(Sort.Direction.DESC, "lastMessageAt")
						.and(Sort.by(Sort.Direction.DESC, "updatedAt")));
		Page<ChatBox> chatBoxes = chatBoxService.getAllChatBoxesForCurrentAccount(pageable);
		return APIResponse.<Page<ChatBox>>builder()
				.result(chatBoxes)
				.build();
	}

	@GetMapping("/{chatBoxId}/messages")
	public APIResponse<Page<ChatMessage>> getMessagesForChatBox(
			@PathVariable String chatBoxId,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ChatMessage> messages = chatMessageService.getMessagesByChatBoxId(chatBoxId, pageable);
		return APIResponse.<Page<ChatMessage>>builder()
				.result(messages)
				.build();
	}

	@PostMapping("/{chatBoxId}/messages/markAsRead")
	public ResponseEntity<?> markMessagesAsReadInChatBox(
			@PathVariable String chatBoxId,
			Principal principal) {
		if (principal == null || principal.getName() == null) {
			System.err.println(
					"[ERROR] ChatBoxController: User principal not found in markMessagesAsReadInChatBox for chatBoxId: "
							+ chatBoxId);
			return ResponseEntity.status(401).body("User not authenticated.");
		}
		String currentUsername = principal.getName();
		chatMessageStatusService.markMessagesAsRead(chatBoxId, currentUsername);

		System.out.println("[DEBUG] ChatBoxController: markMessagesAsReadInChatBox called by user '" + currentUsername
				+ "' for chatBoxId '" + chatBoxId + "'.");
		return ResponseEntity.ok().build();
	}
}
