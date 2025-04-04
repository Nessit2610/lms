package com.husc.lms.dto.response;

import java.util.Date;
import java.util.List;

import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.entity.Lesson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

	private String id;
	
	private String name;
	
	private String description;
	
	private String status; //private or public

	private String learningDurationType; // Thoi han khoa hoc
	
    private Date startDate;

    private Date endDate;

    private String major;
    
    private TeacherResponse teacher;
    
    private List<LessonResponse> lesson;
}
