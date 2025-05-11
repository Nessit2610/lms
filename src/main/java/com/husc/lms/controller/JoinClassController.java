package com.husc.lms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentViewResponse;
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
	
	@GetMapping("/studentrequest")
	public APIResponse<Page<StudentViewResponse>> getAllStudentRequestOfCourse(@RequestParam("courseId") String courseId,
																					@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																					@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(joinClassRequestService.getAllStudentRequestOfCourse(courseId,pageNumber,pageSize))
				.build();
	}
	
	@GetMapping("/courserequest")
	public APIResponse<Page<CourseViewResponse>> getAllCourseRequestOfStudent(@RequestParam("studentId") String studentId,
																			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return APIResponse.<Page<CourseViewResponse>>builder()
				.result(joinClassRequestService.getAllCourseRequestOfStudent(studentId,pageNumber,pageSize))
				.build();
	}
	
	@GetMapping("/getstatus")
	public APIResponse<String> getStatus(@RequestParam("courseId") String courseId) {
		return APIResponse.<String>builder()
				.result(joinClassRequestService.getStatusJoinClass(courseId))
				.build();
	}
	
}
