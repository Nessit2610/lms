package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFileResponse {
	
	private String id;
	
	private String fileName;
	
	private String fileType;

	private String fileUrl;
}
