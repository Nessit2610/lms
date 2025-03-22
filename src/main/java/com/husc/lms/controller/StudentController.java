package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;
	
	@PostMapping("/create")
	public APIResponse<StudentResponse> CreateStudent(@RequestBody StudentRequest request){
		APIResponse<StudentResponse> apiResponse = new APIResponse<StudentResponse>();
		apiResponse.setResult(studentService.createStudent(request));
		return apiResponse;
	}
}
