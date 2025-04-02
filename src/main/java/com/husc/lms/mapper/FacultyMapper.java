package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.FacultyRequest;
import com.husc.lms.dto.response.FacultyResponse;
import com.husc.lms.entity.Faculty;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

	public Faculty toFaculty(FacultyRequest request);
	public FacultyResponse toFacultyResponse(Faculty faculty);
}
