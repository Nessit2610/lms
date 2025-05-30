package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupViewResponse {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private TeacherResponse teacher;
	
}
