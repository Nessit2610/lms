package com.husc.lms.controller;

import java.util.List;

import javax.print.Doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.DocumentResponse;
import com.husc.lms.service.DocumentService;

@RestController
@RequestMapping("/document")
public class DocumentController {

	@Autowired
	private DocumentService documentService;
	
	@PostMapping("/create")
	public APIResponse<DocumentResponse> createDocument(@RequestParam("title") String title,
														@RequestParam("description") String description,
														@RequestParam("majorId") String majorId,
														@RequestParam("file") MultipartFile file,
														@RequestParam("type") String type){
		return APIResponse.<DocumentResponse>builder()
				.result(documentService.createDocument(title, description, description, majorId, file, type))
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteDocument(@RequestParam("documentId") String id){
		return APIResponse.<Boolean>builder()
				.result(documentService.deleteDocument(id))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<DocumentResponse>> getAllPublicDocument(){
		return APIResponse.<List<DocumentResponse>>builder()
				.result(documentService.getAllDocument())
				.build();
	}
	
	@GetMapping("/mydocument")
	public APIResponse<List<DocumentResponse>> getAllMyDocument(){
		return APIResponse.<List<DocumentResponse>>builder()
				.result(documentService.getAllMyDocument())
				.build();
	}
	
	@PutMapping("/updatestatus")
	public APIResponse<Boolean> updateStatus(@RequestParam("documentId") String documentId,
											@RequestParam("status") String status){
		return APIResponse.<Boolean>builder()
				.result(documentService.setStatus(documentId, status))
				.build();
		
	}
}
