package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.service.ChapterService;

@RestController
@RequestMapping("/chapter")
public class ChapterController {

	@Autowired
	private ChapterService chapterService;
	
	
	@PostMapping("/create")
	public APIResponse<ChapterResponse> createChapter(@RequestBody ChapterRequest request){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.createChapter(request))
				.build();
		
	}
	
	
	@PostMapping("/uploads")
	public APIResponse<ChapterResponse> uploadfile(@RequestParam("id") String id,
													@RequestParam("file") MultipartFile file,
													@RequestParam("type")String type){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.uploadFileToChapter(id, file, type))
				.build();
		
	}
}
