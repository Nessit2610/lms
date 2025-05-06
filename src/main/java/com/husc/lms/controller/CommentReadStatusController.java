package com.husc.lms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.CommentReadRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.entity.Comment;
import com.husc.lms.service.CommentReadStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/readStatus")
@RequiredArgsConstructor
public class CommentReadStatusController {
	private final CommentReadStatusService commentReadStatusService;

	@PostMapping("/chapter/read")
	public APIResponse<String> setNotificationAsReadByAccount(@RequestBody CommentReadRequest request) {
	    try {
	        commentReadStatusService.setCommentsAsReadByAccount(request.getComments(), request.getCommentReplies());
	        return APIResponse.<String>builder()
	                .result("Đã đánh dấu đã đọc")
	                .build();
	    } catch (Exception e) {
	        return APIResponse.<String>builder()
	                .result("Có lỗi xảy ra: " + e.getMessage())
	                .build();
	    }
	}

	
}
