package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.SubmitTestRequets;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.TestResultViewResponse;
import com.husc.lms.dto.response.TestStudentResultResponse;
import com.husc.lms.service.TestStudentResultService;

@RestController
@RequestMapping("/teststudentresult")
public class TestStudentResultController {

	@Autowired
	private TestStudentResultService testStudentResultService;
	
	@PostMapping("/starttest")
	public APIResponse<Boolean> startTest(@RequestParam("testId") String testId){
		return APIResponse.<Boolean>builder()
				.result(testStudentResultService.startTest(testId))
				.build();
	}
	
	@PostMapping("/submitTest")
	public APIResponse<Boolean> submitTest(@RequestBody SubmitTestRequets submitTestRequets){
		return APIResponse.<Boolean>builder()
				.result(testStudentResultService.submitTest(submitTestRequets))
				.build();
	}
	
	@GetMapping("/gettestdetail")
	public APIResponse<TestStudentResultResponse> getTestDetail(@RequestParam("testId") String testId){
		return APIResponse.<TestStudentResultResponse>builder()
				.result(testStudentResultService.getDetail(testId))
				.build();
	}
	
	@GetMapping("/getallresult")
	public APIResponse<Page<TestResultViewResponse>> searchCourse(@RequestParam("testId") String testId, 
															@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
    														@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<TestResultViewResponse>>builder()
				.result(testStudentResultService.getAllResult(testId, pageNumber, pageSize))
				.build();
	}
}
