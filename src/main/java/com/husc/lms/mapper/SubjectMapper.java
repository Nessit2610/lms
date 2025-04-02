package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.SubjectResponse;
import com.husc.lms.entity.Subject;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

	public SubjectResponse toSubjectResponse(Subject subject);
}
