package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.response.StudentResponse;

@Mapper(componentModel = "spring")
public interface StudentMapper {

	public StudentResponse toStudentResponse(StudentRequest request);
}
