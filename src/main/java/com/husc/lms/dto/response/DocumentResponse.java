package com.husc.lms.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

	private String id;
		
	private String title;
	
	private String description;
	
	private String status;
	
	private MajorResponse major;
	
	private String fileName;
	
	private String path;
	
	private Object object;
	
	private Date createdAt;
}
