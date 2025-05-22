	package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.service.StudentService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;
	
	@GetMapping("/search")
	public APIResponse<Page<StudentViewResponse>> searchStudent(@RequestParam("fullName") String fullName,
															@RequestParam("email") String email,
															@RequestParam("majorName") String majorName,
															@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
    														@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentService.searchStudents(fullName, email, majorName,pageNumber,pageSize))
				.build();
		
	}
	@GetMapping("/searchnotingroup")
	public APIResponse<Page<StudentViewResponse>> searchStudentNotInGroup(@RequestParam("groupId") String groupId,
																		@RequestParam("keyword") String keyword,
																		@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																		@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentService.searchStudentsNotInGroup(groupId, keyword, pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/myinfo")
	public APIResponse<StudentResponse> getStudentInfo(){
		return APIResponse.<StudentResponse>builder()
				.result(studentService.getStudentInfo())
				.build();
	}
	
	@PostMapping("/create")
	public APIResponse<StudentResponse> CreateStudent(@RequestBody @Valid StudentRequest request){
		return APIResponse.<StudentResponse>builder()
				.result(studentService.createStudent(request))
				.build();
	}
	
	
	@PostMapping("/{id}/upload-photo")
    public APIResponse<String> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return APIResponse.<String>builder()
        		.result(studentService.uploadPhoto(id, file))
        		.build();
    }
	
 	@GetMapping(path = "/image/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
}
