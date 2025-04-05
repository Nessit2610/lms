package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.LessonQuizRequest;
import com.husc.lms.dto.response.LessonQuizResponse;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.LessonQuiz;
import com.husc.lms.mapper.LessonQuizMapper;
import com.husc.lms.repository.LessonQuizRepository;
import com.husc.lms.repository.LessonRepository;

@Service
public class LessonQuizService {

	@Autowired
	private LessonQuizRepository lessonQuizRepository;

	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private LessonQuizMapper lessonQuizMapper;
	
	
	public LessonQuizResponse createLessonQuiz(LessonQuizRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Lesson lesson = lessonRepository.findById(request.getIdLesson()).get();
		
		LessonQuiz lessonQuiz = lessonQuizMapper.toLessonQuiz(request);
					lessonQuiz.setLesson(lesson);
					lessonQuiz.setCreatedBy(name);
					lessonQuiz.setCreatedDate(new Date());
		lessonQuiz = lessonQuizRepository.save(lessonQuiz);
		
		return lessonQuizMapper.toLessonQuizResponse(lessonQuiz);
	}
	

}
