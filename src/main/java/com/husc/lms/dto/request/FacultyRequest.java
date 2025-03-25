package com.husc.lms.dto.request;

import java.util.Date;

import com.husc.lms.entity.Faculty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyRequest {
	
	private String name;
	
	private String code;
}
