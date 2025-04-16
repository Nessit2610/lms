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
public class CourseOfTeacherResponse {

	private String id;
	
	private String image;
	
	private String name;
	
	private String description;
	
	private String status; //private or public

	private String learningDurationType; // Thoi han khoa hoc
	
    private Date startDate;

    private Date endDate;

    private String major;
}
