package com.husc.lms.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.mongoEntity.ChatBox;
import com.husc.lms.mongoEntity.ChatMessage;
import com.husc.lms.mongoRepository.ChatMessageRepository;
import com.husc.lms.mongoService.ChatBoxService;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chatBox")
@RequiredArgsConstructor
public class ChatBoxController {
	private final ChatBoxService chatBoxService;
	
	@GetMapping("")
	public APIResponse<Page<ChatBox>> getAllChatBoxes(
	    @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
	    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
	) {
	    Pageable pageable = PageRequest.of(pageNumber, pageSize);
	    Page<ChatBox> chatBoxes = chatBoxService.getAllChatBoxesForCurrentAccount(pageable);
	    return APIResponse.<Page<ChatBox>>builder()
	    		.result(chatBoxes)
	    		.build();
	}
}
