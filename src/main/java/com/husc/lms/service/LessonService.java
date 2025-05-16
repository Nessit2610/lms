package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.LessonRequest;
import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.dto.update.LessonUpdateRequest;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.StudentLessonProgress;
import com.husc.lms.mapper.LessonMapper;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;

@Service
public class LessonService {

	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LessonMapper lessonMapper;
	
	@Autowired
	private ChapterService chapterService;
	
	@Autowired
	private LessonMaterialService lessonMaterialService;
	
	@Autowired
	private LessonQuizService lessonQuizService;
	
	@Autowired
	private StudentLessonProgressService slpService;
	
	@Autowired
	private StudentLessonProgressRepository slpRepository;
	
	
	public LessonResponse createLesson(LessonRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Course course = courseRepository.findById(request.getCourseId()).get();
		
		Lesson lesson = Lesson.builder()
				.course(course)
				.order(request.getOrder())
				.description(request.getDescription())
				.createdBy(name)
				.createdDate(new Date())
				.build();
		lesson = lessonRepository.save(lesson);
		return lessonMapper.toLessonResponse(lesson);
	}
	
	public boolean deleteLesson(String id) {
		var context = SecurityContextHolder.getContext();
		String nameAccount = context.getAuthentication().getName();
		Lesson lesson = lessonRepository.findByIdAndDeletedDateIsNull(id);
		if(lesson != null) {
			chapterService.deleteChapterByLesson(lesson);
			lessonMaterialService.deleteMaterialByLesson(lesson);
			lessonQuizService.deleteLessonQuizByLesson(lesson);
			lesson.setDeletedBy(nameAccount);
			lesson.setDeletedDate(new Date());
			lessonRepository.save(lesson);
			List<StudentLessonProgress> slpList = slpRepository.findByLesson(lesson);
			if(slpList != null && !slpList.isEmpty()) {
				slpService.deleteLessonProgress(slpList);
			}
			
			return true;
		}
		return false;
	}
	
	public void deleteLessonByCourse(Course course){
		List<Lesson> listLesson = lessonRepository.findByCourseAndDeletedDateIsNull(course);
		for(Lesson l : listLesson) {
			deleteLesson(l.getId());
		}
	}
	
	
	
	public LessonResponse updateLesson(LessonUpdateRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Lesson lesson = lessonRepository.findById(request.getIdLesson()).get();
		lesson.setDescription(request.getDescription());
		lesson.setOrder(request.getOrder());
		lesson.setLastModifiedBy(name);
		lesson.setLastModifiedDate(new Date());
		lesson = lessonRepository.save(lesson);
		return lessonMapper.toLessonResponse(lesson);
	}
}
