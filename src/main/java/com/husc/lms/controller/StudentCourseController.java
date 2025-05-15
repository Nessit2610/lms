package com.husc.lms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.StudentCourseRequest;
import com.husc.lms.dto.request.StudentDeleteRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.service.StudentCourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/studentcourse")
public class StudentCourseController {

	@Autowired
	private StudentCourseService studentCourseService;
	
	@PostMapping("/addstudents")
    public APIResponse<Void> addStudentsToCourse(@RequestBody @Valid StudentCourseRequest request) {
		studentCourseService.addListStudentToCourse(request);
		return APIResponse.<Void>builder()
				.code(200)
				.message("add student success")
				.build();
	}
	
	@GetMapping("/mycourse")
	public APIResponse<Page<CourseViewResponse>> getAllCourseOfStudent(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																		@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<CourseViewResponse>>builder()
				.result(studentCourseService.getAllCourseOfStudent(pageNumber,pageSize))
				.build();
	}
	
	@GetMapping("/studentofcourse")
	public APIResponse<Page<StudentViewResponse>> getAllStudentOfCourse(@RequestParam("courseId") String courseId,
																			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentCourseService.getAllStudentOfCourse(courseId,pageNumber,pageSize))
				.build();
	}
	
	@GetMapping("/searchstudent")
	public APIResponse<Page<StudentViewResponse>> searchStudentInCourse(@RequestParam("courseId") String courseId,
																			@RequestParam("keyword") String keyword,
																			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentCourseService.searchStudentInCourse(courseId,keyword,pageNumber,pageSize))
				.build();
	}
	
	@GetMapping("/searchstudentnotin")
	public APIResponse<Page<StudentViewResponse>> searchStudentNotInCourse(@RequestParam("courseId") String courseId,
																			@RequestParam("keyword") String keyword,
																			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentCourseService.searchStudentNotInCourse(courseId,keyword,pageNumber,pageSize))
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteStudentOfCourse(@RequestParam("courseId") String courseId,
													@RequestParam("studentId") String studendId){
		return APIResponse.<Boolean>builder()
				.result(studentCourseService.deleteStudentOfCourse(studendId, courseId))
				.build();
	}
	
	@DeleteMapping("/deleteall")
	public APIResponse<Boolean> deleteStudentOfCourse(@RequestBody StudentDeleteRequest request){
		return APIResponse.<Boolean>builder()
				.result(studentCourseService.deleteListStudentOfCourse(request))
				.build();
	}
	
}
