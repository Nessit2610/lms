package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.service.JoinClassRequestService;

@RestController
@RequestMapping("/joinclass")
public class JoinClassController {

	@Autowired
	private JoinClassRequestService joinClassRequestService;
	
	@PostMapping("/pending")
	public APIResponse<Boolean> pendingRequest(@RequestParam("courseId") String courseId){
		return APIResponse.<Boolean>builder()
				.result(joinClassRequestService.pendingRequest(courseId))
				.build();
	}
	@PostMapping("/rejected")
	public APIResponse<Boolean> rejectedRequest(@RequestParam("courseId") String courseId,
												@RequestParam("studentId") String studentId){
		return APIResponse.<Boolean>builder()
				.result(joinClassRequestService.rejectedRequest(courseId, studentId))
				.build();
	}
	@PostMapping("/approved")
	public APIResponse<Boolean> approvedRequest(@RequestParam("courseId") String courseId,
												@RequestParam("studentId") String studentId){
		return APIResponse.<Boolean>builder()
				.result(joinClassRequestService.approvedRequest(courseId, studentId))
				.build();
	}
	
	@GetMapping("/studentrequest/{courseId}")
	public APIResponse<List<StudentOfCourseResponse>> getAllStudentRequestOfCourse(@PathVariable("courseId") String courseId) {
		return APIResponse.<List<StudentOfCourseResponse>>builder()
				.result(joinClassRequestService.getAllStudentRequestOfCourse(courseId))
				.build();
	}
	
	@GetMapping("/courserequest/{courseId}")
	public APIResponse<List<CourseViewResponse>> getAllCourseRequestOfStudent(@PathVariable("studentId") String studentId) {
		return APIResponse.<List<CourseViewResponse>>builder()
				.result(joinClassRequestService.getAllCourseRequestOfStudent(studentId))
				.build();
	}
	
	
	
}
