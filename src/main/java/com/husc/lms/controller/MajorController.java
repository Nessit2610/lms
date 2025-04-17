package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.MajorResponse;
import com.husc.lms.service.MajorService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/major")
public class MajorController {

	@Autowired
	private MajorService majorService;
	
	@PostMapping("/create")
	public APIResponse<MajorResponse> createMajor(@RequestParam("majorName") String majorName){
		return APIResponse.<MajorResponse>builder()
				.result(majorService.createMajor(majorName))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<MajorResponse>> getAllMajor() {
		return APIResponse.<List<MajorResponse>>builder()
				.result(majorService.getAllMajor())
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteMajor(@RequestParam("majorId") String id){
		return APIResponse.<Boolean>builder()
				.result(majorService.deleteMajor(id))
				.build();
	}
	
	
}
