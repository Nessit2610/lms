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
public class StudentViewResponse {

	private String id;
	
	private String fullName;
	
	private MajorResponse major;
    
	private String avatar;
	
    private String email;
    
}
