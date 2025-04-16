package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.LessonRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.dto.update.LessonUpdateRequest;
import com.husc.lms.service.LessonService;

@RestController
@RequestMapping("/lesson")
public class LessonController {

	@Autowired
	private LessonService lessonService;
	
	@PostMapping("/create")
	public APIResponse<LessonResponse> createLesson(@RequestBody LessonRequest request){
		return APIResponse.<LessonResponse>builder()
				.result(lessonService.createLesson(request))
				.build();
	}
	
	@PutMapping("/update")
	public APIResponse<LessonResponse> updateLesson(@RequestBody LessonUpdateRequest request){
		return APIResponse.<LessonResponse>builder()
				.result(lessonService.updateLesson(request))
				.build();
	}
	
	@DeleteMapping("/{lessonId}")
	public APIResponse<Boolean> deleteChapter(@PathVariable("lessonId") String id){
		return APIResponse.<Boolean>builder()
				.result(lessonService.deleteLesson(id))
				.build();
	}
}
