package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.TestStudentAnswerResponse;
import com.husc.lms.entity.TestStudentAnswer;

@Mapper(componentModel = "spring")
public interface TestStudentAnswerMapper {

	TestStudentAnswerResponse toTestStudentAnswerResponse(TestStudentAnswer testStudentAnswer);
}
