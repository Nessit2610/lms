package com.husc.lms.dto.update;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {

	private String idCourse;
	
	private String name;

    private String description;

    private Date endDate;

    private String majorId;

    private String status;
    
    private String learningDurationType;
}
