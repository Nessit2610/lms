package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.PostRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.PostResponse;
import com.husc.lms.dto.update.PostUpdateRequest;
import com.husc.lms.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/post")
public class PostController {

	@Autowired
	private PostService postService;
	
	@PostMapping("/create")
	public APIResponse<PostResponse> createPost(@Valid @ModelAttribute PostRequest request){
		return APIResponse.<PostResponse>builder()
				.result(postService.createPost(request))
				.build();
		
	}
	
	@PutMapping("/update")
	public APIResponse<PostResponse> createPost(@Valid @ModelAttribute PostUpdateRequest request){
		return APIResponse.<PostResponse>builder()
				.result(postService.updatePost(request))
				.build();
		
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deletePost(@RequestParam("postId") String postId){
		return APIResponse.<Boolean>builder()
				.result(postService.deletePost(postId))
				.build();
	}
	
	@GetMapping
	public APIResponse<Page<PostResponse>> getAllPostInGroup(@RequestParam("groupId") String groupId ,
															@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
															@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){ 
		return APIResponse.<Page<PostResponse>>builder()
				.result(postService.getAllPostInGroup(groupId, pageNumber, pageSize))
				.build();
	}
	
	
	
}
