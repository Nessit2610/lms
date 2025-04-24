package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.TeacherInfoResponse;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.service.TeacherService;

@RestController
@RequestMapping(("/teacher"))
public class TeacherController {

	@Autowired
	private TeacherService teacherService;
	
	@GetMapping
	public APIResponse<List<TeacherResponse>> getAllTeacher(){
		return APIResponse.<List<TeacherResponse>>builder()
				.result(teacherService.getAllTeacher())
				.build();
	}
	
	@GetMapping("/myinfo")
	public APIResponse<TeacherInfoResponse> getStudentInfo(){
		return APIResponse.<TeacherInfoResponse>builder()
				.result(teacherService.getTeacherInfo())
				.build();
	}

	@PostMapping("/create")
	public APIResponse<TeacherResponse> CreateStudent(@RequestBody TeacherRequest request){
		return APIResponse.<TeacherResponse>builder()
				.result(teacherService.createTeacher(request))
				.build();
	}
	
	
	@PostMapping("/{id}/upload-photo")
    public APIResponse<String> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return APIResponse.<String>builder()
        		.result(teacherService.uploadPhoto(id, file))
        		.build();
    }
	
 	@GetMapping(path = "/image/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
	
}
