package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
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
        															@RequestParam("file") MultipartFile file,
        															@RequestParam("type")String type) {
		return APIResponse.<LessonMaterialResponse>builder()
				.result(lessonMaterialService.createMaterial(id, file, type))
				.build();
	}
	
	// Lưu ý: Type bao gồm {photo , video , file}
	
	@GetMapping(path = "/images/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
	public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
	    return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
	}

	@GetMapping(path = "/videos/{filename}", produces = "video/mp4")
	public byte[] getVideo(@PathVariable("filename") String filename) throws IOException {
	    return Files.readAllBytes(Paths.get(Constant.VIDEO_DIRECTORY + filename));
	}


	@GetMapping("/files/{filename}")
	public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
	    Path path = Paths.get(Constant.FILE_DIRECTORY + filename);
	    byte[] data = Files.readAllBytes(path);

	    String mimeType = Files.probeContentType(path); // Lấy content-type tự động
	    
	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE))
	            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
	            .body(data);
	}

}
