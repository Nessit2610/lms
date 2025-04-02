package com.husc.lms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorRequest {
	
	private String code;
	
	private String moetCode;
	
	private int status;
	
	private String name;
	
	private String facultyid;
	
}
