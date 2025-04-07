package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CourseRequest;
import com.husc.lms.dto.response.CourseResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	
	public CourseResponse createCourse(CourseRequest request) {
		
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		Course course = courseMapper.toCourse(request);
				course.setTeacher(teacher);
				course.setCreatedBy(name);
				course.setCreatedDate(new Date());
				
		course = courseRepository.save(course);			
		return courseMapper.toCourseResponse(course);
		
	}
	
	public CourseResponse getCourseById(String id) {
		Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
		return courseMapper.toCourseResponse(course);
	}
	
	public List<CourseResponse> getAllPublicCourse(){
		List<Course> courses = courseRepository.findByStatus(StatusCourse.PUBLIC.name());
		return courses.stream().map(courseMapper::toCourseResponse).toList();
	}
		
}
