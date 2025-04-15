package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	
	public List<LessonQuizResponse> createLessonQuiz(String idLesson,List<LessonQuizRequest> requestList) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Lesson lesson = lessonRepository.findById(idLesson).get();
		List<LessonQuiz> listLessonQuiz = new ArrayList<LessonQuiz>();
		for(LessonQuizRequest request : requestList) {
			LessonQuiz lessonQuiz = lessonQuizMapper.toLessonQuiz(request);
			lessonQuiz.setLesson(lesson);
			lessonQuiz.setCreatedBy(name);
			lessonQuiz.setCreatedDate(new Date());
			lessonQuiz = lessonQuizRepository.save(lessonQuiz);
			listLessonQuiz.add(lessonQuiz);
		}	
		return listLessonQuiz.stream().map(lessonQuizMapper::toLessonQuizResponse).toList();
	}
	

}
