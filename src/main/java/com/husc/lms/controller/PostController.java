package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.service.PostService;

@RestController
@RequestMapping("/post")
public class PostController {

	@Autowired
	private PostService postService;
	
	@PostMapping("/create")
	public APIResponse<PostResponse> createPost(@RequestParam("groupId") String groupId,
												@RequestParam("title") String title,
												@RequestParam("file") MultipartFile file,
												@RequestParam("type") String type,
												@RequestParam("text") String text){
		return APIResponse.<PostResponse>builder()
				.result(postService.createPost(groupId, title, file, type, text))
				.build();
		
	}
}
