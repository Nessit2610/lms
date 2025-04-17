package com.husc.lms.dto.response;

import java.util.Date;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Document;
import com.husc.lms.entity.Major;

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
	
	
}
