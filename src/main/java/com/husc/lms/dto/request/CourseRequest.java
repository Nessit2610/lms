package com.husc.lms.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {

	private String name;
	
	private Date startDate;
	
	private Date endDate;
	
	private String majorId;
}
