package com.husc.lms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.husc.lms.dto.response.TestStudentAnswerResponse;
import com.husc.lms.entity.TestStudentAnswer;

@Mapper(componentModel = "spring")
public interface TestStudentAnswerMapper {

	@Mapping(source = "correct", target = "correct")
	TestStudentAnswerResponse toTestStudentAnswerResponse(TestStudentAnswer testStudentAnswer);
}
