package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.StudentLessonChapterProgressResponse;
import com.husc.lms.entity.StudentLessonChapterProgress;

@Mapper(componentModel = "spring")
public interface StudentLessonChapterProgressMapper {

	public StudentLessonChapterProgressResponse toResponse(StudentLessonChapterProgress lessonChapterProgress);
}
