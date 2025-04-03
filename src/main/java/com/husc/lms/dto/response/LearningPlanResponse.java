package com.husc.lms.dto.response;

import com.husc.lms.entity.ExamSchedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPlanResponse {

	private CourseSectionResponse courseSection;
	
	//private ExamSchedule examSchedule;
}
