package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.entity.Student;
import com.husc.lms.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;
	
	
	@GetMapping
	public APIResponse<List<StudentResponse>> getAllStudent(){
		return APIResponse.<List<StudentResponse>>builder()
				.result(studentService.getAllStudent())
				.build();
	}
	
	@GetMapping("/myinfo")
	public APIResponse<StudentResponse> getStudentInfo(){
		return APIResponse.<StudentResponse>builder()
				.result(studentService.getStudentInfo())
				.build();
	}
	
	@PostMapping("/create")
	public APIResponse<StudentResponse> CreateStudent(@RequestBody StudentRequest request){
		APIResponse<StudentResponse> apiResponse = new APIResponse<StudentResponse>();
		apiResponse.setResult(studentService.createStudent(request));
		return apiResponse;
	}
	
	
	@PostMapping("/{id}/upload-photo")
    public APIResponse<String> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return APIResponse.<String>builder()
        		.result(studentService.uploadPhoto(id, file))
        		.build();
    }
	
	

	    
	
}
