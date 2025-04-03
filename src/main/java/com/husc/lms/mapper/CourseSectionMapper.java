package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.CourseSectionResponse;
import com.husc.lms.entity.CourseSection;

@Mapper(componentModel = "srping")
public interface CourseSectionMapper {
	public CourseSectionResponse toCourseSectionResponse(CourseSection courseSection);
}
