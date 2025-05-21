package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.LessonRequest;
import com.husc.lms.dto.response.LessonResponse;
import com.husc.lms.dto.update.LessonUpdateRequest;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentLessonProgress;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.mapper.LessonMapper;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.NotificationRepository;
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
	private NotificationRepository notificationRepository;

	@Autowired
	private NotificationService notificationService;
	
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
		
		List<Student> studentsInCourse = course.getStudentCourses().stream()
				.map(studentCourse -> studentCourse.getStudent())
				.collect(Collectors.toList());
		// Tạo notification cho các sinh viên trong group
		for (Student student : studentsInCourse) {
			Notification notification = Notification.builder()
					.account(student.getAccount())
					.description(
							"Giảng viên " + course.getTeacher().getFullName() + " vừa đăng 1 chương mới trong khoá học " + course.getName())
					.type(NotificationType.JOIN_CLASS_PENDING)
					.createdAt(OffsetDateTime.now())
					.build();
			notificationRepository.save(notification);

			// Gửi thông báo qua WebSocket
			Map<String, Object> payload = new HashMap<>();
			payload.put("receivedAccount", student.getAccount().getUsername());
			payload.put("message",
					"Giảng viên " + course.getTeacher().getFullName() + " vừa đăng chương mới trong nhóm " + course.getName());
			payload.put("type", NotificationType.POST_CREATED.name());
			payload.put("courseId", course.getId());
			payload.put("lessonId", lesson.getId());
			payload.put("createdAt", OffsetDateTime.now());

			notificationService.sendCustomWebSocketNotificationToUser(student.getAccount().getUsername(), payload);
		}
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
