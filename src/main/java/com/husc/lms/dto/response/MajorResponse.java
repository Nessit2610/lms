package com.husc.lms.dto.response;


import com.husc.lms.entity.Faculty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorResponse {
	
	private String code;
	
	private String moetCode;
	
	private int status;
	
	private String name;
	
	private FacultyResponse faculty;
	
}
