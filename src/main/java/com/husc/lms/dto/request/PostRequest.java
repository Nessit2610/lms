package com.husc.lms.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {
	
	@NotNull
	private String groupId;
	
    private String title;
    
    private String text;
    
    private List<FileUploadRequest> fileUploadRequests;
}
