package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.service.CourseService;

@RestController
@RequestMapping("/course")
public class CourseController {

	@Autowired
	private CourseService courseService;

	@PostMapping("/create")
	public APIResponse<CourseResponse> createCourse(@RequestBody CourseRequest request){
		return APIResponse.<CourseResponse>builder()
				.result(courseService.createCourse(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<CourseResponse>> getAllCourse() {
		return APIResponse.<List<CourseResponse>>builder()
				.result(courseService.getAllCourse())
				.build();
	}
}
