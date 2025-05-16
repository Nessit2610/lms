package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.ChapterRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.ChapterResponse;
import com.husc.lms.dto.update.ChapterUpdateRequest;
import com.husc.lms.service.ChapterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chapter")
public class ChapterController {

	@Autowired
	private ChapterService chapterService;
	
	
	@PostMapping("/create")
	public APIResponse<ChapterResponse> createChapter(@ModelAttribute @Valid ChapterRequest chapterRequest){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.createChapter(chapterRequest))
				.build();
		
	}	
	// Luu y, type : image , video , file
	
	@PutMapping("/update")
	public APIResponse<ChapterResponse> updateChapter(@RequestBody @Valid ChapterUpdateRequest request){
		return APIResponse.<ChapterResponse>builder()
				.result(chapterService.updateChapter(request))
				.build();
	}
	
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteChapter(@RequestParam("chapterId") String id){
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
