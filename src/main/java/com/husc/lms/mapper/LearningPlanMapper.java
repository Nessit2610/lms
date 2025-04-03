package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.LearningPlanResponse;
import com.husc.lms.entity.LearningPlan;

@Mapper(componentModel = "spring")
public interface LearningPlanMapper {

	public LearningPlanResponse toLearningPlanResponse(LearningPlan learningPlan);
}
