package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.CurriculumSubjectResponse;
import com.husc.lms.entity.CurriculumSubject;

@Mapper(componentModel = "spring")
public interface CurriculumSubjectMapper {

	public CurriculumSubjectResponse toCurriculumSubjectResponse(CurriculumSubject curriculumSubject);
}
