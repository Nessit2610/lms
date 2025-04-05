package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.LessonQuizRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.LessonQuizResponse;
import com.husc.lms.service.LessonQuizService;

@RestController
@RequestMapping("/lessonquiz")
public class LessonQuizController {

	@Autowired
	private LessonQuizService lessonQuizService;
	
	@PostMapping("/create")
	public APIResponse<LessonQuizResponse> createLessonQuiz(@RequestBody LessonQuizRequest request){
		return APIResponse.<LessonQuizResponse>builder()
				.result(lessonQuizService.createLessonQuiz(request))
				.build();
	}
	
}
