package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.TestQuestionRequest;
import com.husc.lms.dto.response.TestQuestionResponse;
import com.husc.lms.entity.TestQuestion;

@Mapper(componentModel = "spring")
public interface TestQuestionMapper {

	TestQuestion toTestQuestion(TestQuestionRequest request);
	TestQuestionResponse toTestQuestionResponse(TestQuestion testQuestion);
}
