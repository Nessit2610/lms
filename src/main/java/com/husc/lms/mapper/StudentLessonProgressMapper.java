package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.StudentLessonProgressResponse;
import com.husc.lms.entity.StudentLessonProgress;

@Mapper(componentModel = "spring")
public interface StudentLessonProgressMapper {

	public StudentLessonProgressResponse toStudentLessonProgressResponse(StudentLessonProgress studentLessonProgress);
	
}
