package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.StudentLessonProgressResponse;
import com.husc.lms.service.StudentLessonProgressService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/lessonprogress")
public class StudentLessonProgressController {

	@Autowired
	private StudentLessonProgressService studentLessonProgressService;
	
	@PostMapping("/savelessonprogress/{lessonId}")
	public APIResponse<StudentLessonProgressResponse> saveLessonProgress(@PathVariable("lessonId") String id){
		return APIResponse.<StudentLessonProgressResponse>builder()
				.result(studentLessonProgressService.saveProgressLesson(id))
				.build();
	}
	
	
	@PutMapping("/completelesson/{lessonId}")
	public APIResponse<StudentLessonProgressResponse> completeLesson(@PathVariable("lessonId") String id) {
		return APIResponse.<StudentLessonProgressResponse>builder()
				.result(studentLessonProgressService.setCompletedLesson(id))
				.build();
	}
	
	@GetMapping("/getprogress/{lessonId}")
	public APIResponse<StudentLessonProgressResponse> getProgress(@PathVariable("lessonId") String id) {
		return APIResponse.<StudentLessonProgressResponse>builder()
				.result(studentLessonProgressService.getLessonProgress(id))
				.build();
	}
	
}
