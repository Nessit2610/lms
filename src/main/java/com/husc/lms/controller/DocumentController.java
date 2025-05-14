package com.husc.lms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.constant.Constant;
import com.husc.lms.dto.request.DocumentRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.DocumentResponse;
import com.husc.lms.service.DocumentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	private DocumentService documentService;
	
	@PostMapping("/create")
	public APIResponse<DocumentResponse> createDocument(@ModelAttribute @Valid DocumentRequest request){
		return APIResponse.<DocumentResponse>builder()
				.result(documentService.createDocument(request))
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteDocument(@RequestParam("documentId") String id){
		return APIResponse.<Boolean>builder()
				.result(documentService.deleteDocument(id))
				.build();
	}
	
	@GetMapping
	public APIResponse<Page<DocumentResponse>> getAllPublicDocument(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																	@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<DocumentResponse>>builder()
				.result(documentService.getAllDocument(pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/search")
	public APIResponse<Page<DocumentResponse>> searchDocument(@RequestParam("keyword") String keyword ,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<DocumentResponse>>builder()
				.result(documentService.searchDocument(keyword, pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/getbymajor")
	public APIResponse<Page<DocumentResponse>> findDocumentByMajor(@RequestParam("majorId") String majorId ,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<DocumentResponse>>builder()
				.result(documentService.findDocumentByMajor(majorId, pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/mydocument")
	public APIResponse<Page<DocumentResponse>> getAllMyDocument(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<DocumentResponse>>builder()
				.result(documentService.getAllMyDocument(pageNumber, pageSize))
				.build();
	}
	
	@PutMapping("/updatestatus")
	public APIResponse<Boolean> updateStatus(@RequestParam("documentId") String documentId,
											@RequestParam("status") String status){
		return APIResponse.<Boolean>builder()
				.result(documentService.setStatus(documentId, status))
				.build();
		
	}
	@GetMapping("/videos/{filename}")
	public ResponseEntity<Resource> streamVideo(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        return documentService.streamVideo(filename, rangeHeader);
    }

	@GetMapping("/files/{filename}")
	public ResponseEntity<byte[]> getFile(@PathVariable String filename) throws IOException {
	    return documentService.getFile(filename);
	}
	
	
	@GetMapping(path = "/image/{filename}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }
}
