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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ChatBoxResponse;
import com.husc.lms.dto.response.ChatMessageResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoEntity.ChatBoxMember;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatBoxService;
import com.husc.lms.mongoService.ChatMessageService;
import com.husc.lms.mongoService.ChatMessageStatusService;
import com.husc.lms.mongoService.ChatBoxMemberService;
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
	private final ChatBoxMemberService chatBoxMemberService;

	@GetMapping("")
	public APIResponse<Page<ChatBoxResponse>> getAllChatBoxes(
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		Page<ChatBoxResponse> chatBoxes = chatBoxService.getAllChatBoxesForCurrentAccount(pageNumber, pageSize);
		return APIResponse.<Page<ChatBoxResponse>>builder()
				.result(chatBoxes)
				.build();
	}

	@GetMapping("/{chatBoxId}/messages")
	public APIResponse<Page<ChatMessageResponse>> getMessagesForChatBox(
			@PathVariable String chatBoxId,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
		Page<ChatMessageResponse> messages = chatMessageService.getMessagesByChatBoxId(chatBoxId, pageNumber, pageSize);
		return APIResponse.<Page<ChatMessageResponse>>builder()
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

	@GetMapping("/{chatBoxId}/members")
	public APIResponse<List<ChatBoxMember>> getChatBoxMembers(@PathVariable String chatBoxId) {
		List<ChatBoxMember> members = chatBoxMemberService.getChatBoxMembersByChatBoxId(chatBoxId);
		return APIResponse.<List<ChatBoxMember>>builder()
				.result(members)
				.build();
	}

	@DeleteMapping("/{chatBoxId}/members/{memberUsername}")
	public ResponseEntity<?> removeMemberFromChatBox(
			@PathVariable String chatBoxId,
			@PathVariable String memberUsername,
			Principal principal) {

		if (principal == null || principal.getName() == null) {
			System.err.println(
					"[ERROR] ChatBoxController: User principal not found in removeMemberFromChatBox for chatBoxId: "
							+ chatBoxId + ", member to remove: " + memberUsername);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("User not authenticated for remove member operation.");
		}
		String requestorUsername = principal.getName();

		chatBoxMemberService.removeMemberFromChatBox(chatBoxId, memberUsername, requestorUsername);

		System.out.println("[DEBUG] ChatBoxController: removeMemberFromChatBox called by user '" + requestorUsername
				+ "' for chatBoxId '" + chatBoxId + "' to remove member '" + memberUsername + "'.");
		return ResponseEntity.ok().build();
	}

	@GetMapping("/searchGroupByName")
	public APIResponse<Page<ChatBoxResponse>> searchGroupByName(
			@RequestParam(name = "name") String name,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
		Page<ChatBoxResponse> result = chatBoxService.searchByNameOfChatBox(name, pageNumber, pageSize);
		return APIResponse.<Page<ChatBoxResponse>>builder().result(result).build();
	}

	@PutMapping("/rename")
	public APIResponse<ChatBox> renameChatBox(
			@RequestParam String chatBoxId,
			@RequestParam String newName) {
		ChatBox updated = chatBoxService.renameChatBox(chatBoxId, newName);
		return APIResponse.<ChatBox>builder().result(updated).build();
	}

}
