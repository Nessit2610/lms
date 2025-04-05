package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.LessonQuizRequest;
import com.husc.lms.dto.response.LessonQuizResponse;
import com.husc.lms.entity.LessonQuiz;

@Mapper(componentModel = "spring")
public interface LessonQuizMapper {

	public LessonQuiz toLessonQuiz(LessonQuizRequest request);
	
	public LessonQuizResponse toLessonQuizResponse(LessonQuiz lessonQuiz);
}
