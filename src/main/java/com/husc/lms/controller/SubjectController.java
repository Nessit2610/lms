package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.SubjectResponse;
import com.husc.lms.service.SubjectService;

@RestController
@RequestMapping("/subject")
public class SubjectController {

	@Autowired
	private SubjectService subjectService;
	
	@GetMapping
	public APIResponse<List<SubjectResponse>> getAllSubject(){
		return APIResponse.<List<SubjectResponse>>builder()
				.result(subjectService.getAllSubject())
				.build();
	}
}
