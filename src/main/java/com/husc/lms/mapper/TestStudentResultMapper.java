package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.TestStudentResultResponse;
import com.husc.lms.entity.TestStudentResult;

@Mapper(componentModel = "spring")
public interface TestStudentResultMapper {

	TestStudentResultResponse toTestStudentResultResponse(TestStudentResult testStudentResult);
}
