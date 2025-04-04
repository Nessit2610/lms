package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.LessonMaterialResponse;
import com.husc.lms.service.LessonMaterialService;

@RestController
@RequestMapping("/lessonmaterial")
public class LessonMaterialController {

	
	@Autowired
	private LessonMaterialService lessonMaterialService;
	
	@PostMapping("/create")
	public APIResponse<LessonMaterialResponse> createLessonMaterial(@RequestParam("id") String id,
            													@RequestParam("file") MultipartFile file) {
		return APIResponse.<LessonMaterialResponse>builder()
				.result(lessonMaterialService.createMaterial(id, file))
				.build();
	}
	
	@GetMapping("/{filename}")
	public ResponseEntity<Resource> getVideo(@PathVariable String filename) throws IOException {
	    Path videoPath = Paths.get(Constant.VIDEO_DIRECTORY).resolve(filename);
	    Resource videoResource = new UrlResource(videoPath.toUri());

	    if (videoResource.exists() || videoResource.isReadable()) {
	        InputStreamResource resource = new InputStreamResource(videoResource.getInputStream());
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Đảm bảo định dạng của file
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
	                .body(resource);
	    } else {
	        throw new RuntimeException("Video not found: " + filename);
	    }
	}

}
