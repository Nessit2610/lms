package com.husc.lms.dto.response;

import com.husc.lms.entity.Major;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {
	
	private String id;
	private String fullName;			
	private String email;
	private Major major;
	private String description;
	private String avatar;
	
	
}
