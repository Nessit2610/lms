package com.husc.lms.dto.update;

import java.util.List;

import com.husc.lms.dto.request.FileUploadRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

	@NotNull
	private String postId;
	
    private String title;
    
    private String text;
    
    private List<String> oldFileNames;
    
    private List<FileUploadRequest> fileUploadRequests;
}
