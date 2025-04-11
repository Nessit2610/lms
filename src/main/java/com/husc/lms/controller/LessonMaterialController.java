package com.husc.lms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.husc.lms.configuration.LimitedInputStream;
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
	
	@GetMapping("/videos/{filename}")
	public ResponseEntity<Resource> streamVideo(
	        @PathVariable String filename,
	        @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

	    File videoFile = Paths.get(Constant.VIDEO_DIRECTORY + filename).toFile();
	    long fileLength = videoFile.length();

	    // Không có Range: trả toàn bộ video
	    if (rangeHeader == null) {
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
	                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
	                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
	                .body(new FileSystemResource(videoFile));
	    }

	    // Có Range: xử lý Partial Content
	    long start, end;
	    String[] ranges = rangeHeader.replace("bytes=", "").split("-");
	    start = Long.parseLong(ranges[0]);
	    end = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;

	    long contentLength = end - start + 1;
	    InputStream inputStream = new FileInputStream(videoFile);
	    inputStream.skip(start);
	    InputStreamResource inputStreamResource = new InputStreamResource(new LimitedInputStream(inputStream, contentLength));

	    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
	            .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
	            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
	            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
	            .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength)
	            .body(inputStreamResource);
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
