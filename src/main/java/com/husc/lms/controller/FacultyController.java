package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.FacultyRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.FacultyResponse;
import com.husc.lms.service.FacultyService;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

	@Autowired
	private  FacultyService facultyService;
	
	@PostMapping("/create")
	public APIResponse<FacultyResponse> createFaculty(@RequestBody FacultyRequest request){
		return APIResponse.<FacultyResponse>builder()
				.result(facultyService.createFaculty(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<FacultyResponse>> getAll() {
		return APIResponse.<List<FacultyResponse>>builder()
				.result(facultyService.getAllFaculty())
				.build();
	}
	
}
