package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.MajorRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.MajorResponse;
import com.husc.lms.service.MajorService;

@RestController
@RequestMapping("/major")
public class MajorController {

	@Autowired
	private MajorService majorService;
	
	@PostMapping("/create")
	public APIResponse<MajorResponse> createMajor(@RequestBody MajorRequest request) {
		return APIResponse.<MajorResponse>builder()
				.result(majorService.createMajor(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<MajorResponse>> getAllMajor(){
		return APIResponse.<List<MajorResponse>>builder()
				.result(majorService.getAllMajor())
				.build();
	}
	
}
