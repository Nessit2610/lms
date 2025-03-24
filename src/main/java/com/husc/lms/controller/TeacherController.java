package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.response.APIResponse;

import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.service.TeacherService;

@RestController
@RequestMapping(("/teacher"))
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@PostMapping("/create")
	public APIResponse<TeacherResponse> CreateStudent(@RequestBody TeacherRequest request){
		APIResponse<TeacherResponse> apiResponse = new APIResponse<TeacherResponse>();
		apiResponse.setResult(teacherService.createTeacher(request));
		return apiResponse;
	}
}
