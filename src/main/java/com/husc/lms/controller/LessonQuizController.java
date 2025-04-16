package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@PostMapping("{idLesson}/create")
	public APIResponse<List<LessonQuizResponse>> createLessonQuiz(@PathVariable("idLesson") String idLesson, @RequestBody List<LessonQuizRequest> request){
		return APIResponse.<List<LessonQuizResponse>>builder()
				.result(lessonQuizService.createLessonQuiz(idLesson,request))
				.build();
	}
	@DeleteMapping("/{lessonQuizId}")
	public APIResponse<Boolean> deleteChapter(@PathVariable("lessonQuizId") String id){
		return APIResponse.<Boolean>builder()
				.result(lessonQuizService.deleteLessonQuiz(id))
				.build();
	}
}
