package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.entity.Lesson;

@Mapper(componentModel = "spring")
public interface LessonMapper {

	public LessonResponse toLessonResponse(Lesson lesson);
}
