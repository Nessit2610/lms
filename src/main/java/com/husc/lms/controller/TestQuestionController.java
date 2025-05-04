package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.service.TestQuestionService;

@RestController
@RequestMapping("/testquestion")
public class TestQuestionController {

	@Autowired
	private TestQuestionService testQuestionService;
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteQuestion(@RequestParam("questionId") String id){
		return APIResponse.<Boolean>builder()
				.result(testQuestionService.deleteQuestionById(id))
				.build();
	}
	
	
}
