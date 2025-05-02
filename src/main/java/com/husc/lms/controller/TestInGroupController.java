package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.TestInGroupRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.service.TestInGroupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("testingroup")
public class TestInGroupController {

	@Autowired
	private TestInGroupService testInGroupService;
	
	@PostMapping("/create")
	public APIResponse<TestInGroupResponse> createTest(@RequestBody @Valid TestInGroupRequest request){
		return APIResponse.<TestInGroupResponse>builder()
				.result(testInGroupService.createTestInGroup(request))
				.build();
	}
	
}
