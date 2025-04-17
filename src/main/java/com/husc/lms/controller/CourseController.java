package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.update.CourseUpdateRequest;
import com.husc.lms.service.CourseService;

@RestController
@RequestMapping("/course")
public class CourseController {

	@Autowired
	private CourseService courseService;
	
	@GetMapping
	public APIResponse<List<CourseViewResponse>> getAllPublicCourse(){
		return APIResponse.<List<CourseViewResponse>>builder()
				.result(courseService.getAllPublicCourse())
				.build();
	}
	

	@GetMapping("/courseofteacher")
	public APIResponse<List<CourseViewResponse>> getCourseOfTeacher(){
		return APIResponse.<List<CourseViewResponse>>builder()
				.result(courseService.getCourseOfTeacher())
				.build();
	}
	
	@PutMapping("/update")
	public APIResponse<CourseResponse> updateCourse(@RequestBody CourseUpdateRequest request){
		return APIResponse.<CourseResponse>builder()
				.result(courseService.updateCourse(request))
				.build();
	}
	
	@DeleteMapping("/{courseId}")
	public APIResponse<Boolean> deleteChapter(@PathVariable("courseId") String id){
		return APIResponse.<Boolean>builder()
				.result(courseService.deleteCourse(id))
				.build();
	}
	
	@PostMapping("/create")
	public APIResponse<CourseResponse> createCourse(@RequestBody CourseRequest request){
		return APIResponse.<CourseResponse>builder()
				.result(courseService.createCourse(request))
				.build();
	}
	
	@GetMapping("/{id}")
	public APIResponse<CourseResponse> getCourseById(@PathVariable("id") String id){
		return APIResponse.<CourseResponse>builder()
				.result(courseService.getCourseById(id))
				.build();
	}
	
	@PostMapping("/{id}/upload-photo")
    public APIResponse<String> uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return APIResponse.<String>builder()
        		.result(courseService.uploadPhoto(id, file))
        		.build();
    }
	
 	@GetMapping(path = "/image/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
}
