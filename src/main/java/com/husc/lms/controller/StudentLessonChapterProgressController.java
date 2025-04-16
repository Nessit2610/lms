package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.StudentLessonChapterProgressResponse;
import com.husc.lms.service.StudentLessonChapterProgressService;


@RestController
@RequestMapping("/lessonchapterprogress")
public class StudentLessonChapterProgressController {

	@Autowired
	private StudentLessonChapterProgressService slcpService;
	
	@PostMapping("/savechapterprogress/{chapterId}")
	public APIResponse<StudentLessonChapterProgressResponse> saveLessonProgress(@PathVariable("chapterId") String id){
		return APIResponse.<StudentLessonChapterProgressResponse>builder()
				.result(slcpService.saveChapterProgress(id))
				.build();
	}
	
	@PutMapping("/completechapter/{chapterId}")
	public APIResponse<StudentLessonChapterProgressResponse> completeLesson(@PathVariable("chapterId") String id) {
		return APIResponse.<StudentLessonChapterProgressResponse>builder()
				.result(slcpService.setCompleteChapter(id))
				.build();
	}
	
	@GetMapping("/getprogress/{chapterId}")
	public APIResponse<StudentLessonChapterProgressResponse> getProgress(@PathVariable("chapterId") String id) {
		return APIResponse.<StudentLessonChapterProgressResponse>builder()
				.result(slcpService.getChapterProgress(id))
				.build();
	}
}
