package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.ClassRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ClassResponse;
import com.husc.lms.entity.Student;
import com.husc.lms.service.ClassService;

@RestController
@RequestMapping("/class")
public class ClassController {

	@Autowired
	private ClassService classService;
	
	
	@PostMapping("/create")
	public APIResponse<ClassResponse> createClass(@RequestBody ClassRequest request){
		return APIResponse.<ClassResponse>builder()
				.result(classService.createClass(request))
				.build();
	}
	
	@PostMapping("/{classId}")
	public APIResponse<ClassResponse> addStudent(@PathVariable("classId") String id, @RequestBody List<String> listStudent){
		return APIResponse.<ClassResponse>builder()
				.result(classService.addStudentToClass(listStudent, id))
				.build();
	}
	
	
	
	@GetMapping("/{classId}")
	public APIResponse<ClassResponse> getClass(@PathVariable("classId") String id){
		return APIResponse.<ClassResponse>builder()
				.result(classService.getClass(id))
				.build();
	}
}
