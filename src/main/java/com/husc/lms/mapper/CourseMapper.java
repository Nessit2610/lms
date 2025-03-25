package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {
	public CourseResponse toCourseResponse(Course course);
}
