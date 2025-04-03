package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.AcademicYearResponse;
import com.husc.lms.entity.AcademicYear;

@Mapper(componentModel = "spring")
public interface AcademicYearMapper {

	public AcademicYearResponse toAcademicYearResponse(AcademicYear academicYear);
}
