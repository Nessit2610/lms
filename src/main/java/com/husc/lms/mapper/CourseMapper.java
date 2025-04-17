package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.update.CourseUpdateRequest;
import com.husc.lms.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {


	public Course toCourse(CourseRequest request);
	
	public Course toCourse(CourseUpdateRequest request);
	
	public CourseResponse toCourseResponse(Course course);
	
	public CourseViewResponse toCourseViewResponse(Course course);
	
}
