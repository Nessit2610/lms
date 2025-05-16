package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.TestInGroupRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.dto.response.TestInGroupViewResponse;
import com.husc.lms.dto.update.TestInGroupUpdateRequest;
import com.husc.lms.service.TestInGroupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/testingroup")
public class TestInGroupController {

	@Autowired
	private TestInGroupService testInGroupService;
	
	@PostMapping("/create")
	public APIResponse<TestInGroupResponse> createTest(@RequestBody @Valid TestInGroupRequest request){
		return APIResponse.<TestInGroupResponse>builder()
				.result(testInGroupService.createTestInGroup(request))
				.build();
	}
	
	@PutMapping("/update")
	public APIResponse<TestInGroupResponse> updateTest(@RequestBody @Valid TestInGroupUpdateRequest request){
		return APIResponse.<TestInGroupResponse>builder()
				.result(testInGroupService.updateTestInGroup(request))
				.build();
	}
	
	@GetMapping("/getalltest")
	public APIResponse<Page<TestInGroupViewResponse>> getAllTestInGroup(@RequestParam("groupId") String groupId,
																		@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																		@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<TestInGroupViewResponse>>builder()
				.result(testInGroupService.getAllTestInGroup(groupId, pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/testdetails")
	public APIResponse<TestInGroupResponse> getTestDetails(@RequestParam("testId") String testId){
		return APIResponse.<TestInGroupResponse>builder()
				.result(testInGroupService.getById(testId))
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteTestInGroup(@RequestParam("testId") String testId){
		return APIResponse.<Boolean>builder()
				.result(testInGroupService.deleteTestInGroup(testId))
				.build();
	}
	
}
