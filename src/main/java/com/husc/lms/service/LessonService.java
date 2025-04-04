package com.husc.lms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.LessonRequest;
import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.mapper.LessonMapper;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;

@Service
public class LessonService {

	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LessonMapper lessonMapper;
	
	public LessonResponse createLesson(LessonRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Course course = courseRepository.findById(request.getCourseId()).get();
		
		Lesson lesson = Lesson.builder()
				.course(course)
				.order(request.getOrder())
				.description(request.getDescription())
				.createdBy(name)
				.build();
		lesson = lessonRepository.save(lesson);
		return lessonMapper.toLessonResponse(lesson);
	}
}
