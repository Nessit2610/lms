package com.husc.lms.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentRequest {

	@NotNull(message = "NOT_NULL")
	private String title;
	
	private String description;
	
	@NotNull(message = "NOT_NULL")
	private String status;
	
	@NotNull(message = "NOT_NULL")
	private String majorId;
	
	@NotNull(message = "NOT_NULL")
	private MultipartFile file;
	
	@NotNull(message = "NOT_NULL")
	private String type;
}
