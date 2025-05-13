package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.service.PostFileService;

@RestController
@RequestMapping("/postfile")
public class PostFileController {

	@Autowired
	private PostFileService postFileService;
	
	@GetMapping("/videos/{filename}")
	public ResponseEntity<Resource> streamVideo(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        return postFileService.streamVideo(filename, rangeHeader);
    }

	@GetMapping("/files/{filename}")
	public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
	    return postFileService.getFile(filename);
	}
	
	
	@GetMapping(path = "/images/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
	
}
