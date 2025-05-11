package com.husc.lms.dto.request;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterRequest {
	
	@NotNull(message = "NOT_NULL")
	private String lessonId;

	@NotNull(message = "NOT_NULL")
	private String name;
    
	@NotNull(message = "NOT_NULL")
    private int order;
    
	@NotNull(message = "NOT_NULL")
    private MultipartFile file;
    
	@NotNull(message = "NOT_NULL")
    private String type;
}
