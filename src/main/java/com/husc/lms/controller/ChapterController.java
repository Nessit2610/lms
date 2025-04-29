package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.configuration.LimitedInputStream;
import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.service.ChapterService;

@RestController
@RequestMapping("/chapter")
public class ChapterController {

	@Autowired
	private ChapterService chapterService;
	
	
	@PostMapping("/create")
	public APIResponse<ChapterResponse> createChapter(@RequestParam("lessonId") String lessonid,
													@RequestParam("name") String name,
													@RequestParam("order") int order,
													@RequestParam("file") MultipartFile file,
													@RequestParam("type")String type){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.createChapter(lessonid, name, order, file, type))
				.build();
		
	}	
	// Luu y, type : image , video , file
	
	@PutMapping("/update")
	public APIResponse<ChapterResponse> updateChapter(@RequestParam("chapterId") String chapterId,
			@RequestParam("name") String name,
			@RequestParam("order") int order,
			@RequestParam("file") MultipartFile file,
			@RequestParam("type")String type){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.updateChapter(chapterId, name, order, file, type))
				.build();
	}
	
	
	@DeleteMapping("/{chapterId}")
	public APIResponse<Boolean> deleteChapter(@PathVariable("chapterId") String id){
		return APIResponse.<Boolean>builder()
				.result(chapterService.deleteChapter(id))
				.build();
	}
	
	
	@GetMapping("/videos/{filename}")
	public ResponseEntity<Resource> streamVideo(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        return chapterService.streamVideo(filename, rangeHeader);
    }

	@GetMapping("/files/{filename}")
	public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
	    return chapterService.getFile(filename);
	}
	
	@GetMapping(path = "/images/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
}
