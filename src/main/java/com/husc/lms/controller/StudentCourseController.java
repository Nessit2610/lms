package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.StudentCourseRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.service.StudentCourseService;

@RestController
@RequestMapping("/studentcourse")
public class StudentCourseController {

	@Autowired
	private StudentCourseService studentCourseService;
	
	@PostMapping("/addstudents")
    public APIResponse<Void> addStudentsToCourse(@RequestBody StudentCourseRequest request) {
		studentCourseService.addListStudentToCourse(request);
		return APIResponse.<Void>builder()
				.code(200)
				.message("add student success")
				.build();
	}
	
	@GetMapping("/mycourse")
	public APIResponse<List<CourseViewResponse>> getAllCourseOfStudent(){
		return APIResponse.<List<CourseViewResponse>>builder()
				.result(studentCourseService.getAllCourseOfStudent())
				.build();
	}
	
	@GetMapping("/studentofcourse/{courseId}")
	public APIResponse<List<StudentOfCourseResponse>> getAllStudentOfCourse(@PathVariable("courseId") String courseId){
		return APIResponse.<List<StudentOfCourseResponse>>builder()
				.result(studentCourseService.getAllStudentOfCourse(courseId))
				.build();
	}
//	@GetMapping("/studentnotincourse/{courseId}")
//	public APIResponse<List<StudentOfCourseResponse>> getAllStudentNotInCourse(@PathVariable("courseId") String courseId){
//		return APIResponse.<List<StudentOfCourseResponse>>builder()
//				.result(studentCourseService.getAllStudentNotInCourse(courseId))
//				.build();
//	}
}
