package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.JoinClassStatus;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.JoinClassRequestRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class JoinClassRequestService {

	@Autowired
	private JoinClassRequestRepository joinClassRequestRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private StudentCourseService studentCourseService;
	
	@Transactional
	public boolean pendingRequest(String courseId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account);
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(course.getStatus().contains(StatusCourse.PRIVATE.name())) {
			throw new AppException(ErrorCode.CAN_NOT_REQUEST);
		}
		
		if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.PENDING.name())
		|| joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.APPROVED.name())) {
			throw new AppException(ErrorCode.REQUEST_EXIST);
		}
	
		if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.REJECTED.name())) {
			joinClassRequestRepository.deleteByStudentAndCourse(student, course);
		}
		if(student != null && course != null) {
			JoinClassRequest joinClassRequest = JoinClassRequest.builder()
					.student(student)
					.course(course)
					.status(JoinClassStatus.PENDING.name())
					.createdAt(new Date())
					.build();
			joinClassRequestRepository.save(joinClassRequest);
			return true;
		}
		return false;
	}
	
	public boolean rejectedRequest(String courseId, String studentId) {
		
		Student student = studentRepository.findById(studentId).get();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(student != null && course != null) {
			JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
			joinClassRequest.setStatus(JoinClassStatus.REJECTED.name());
			joinClassRequestRepository.save(joinClassRequest);
			return true;
		}
		return false;
	}
	
	public boolean approvedRequest(String courseId,String studentId) {
		Student student = studentRepository.findById(studentId).get();
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
		if(student != null && course != null) {
			JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
			joinClassRequest.setStatus(JoinClassStatus.APPROVED.name());
			joinClassRequestRepository.save(joinClassRequest);
			studentCourseService.addStudentToCourse(student, course);
			return true;
		}
		return false;
	}
	
	public Page<StudentOfCourseResponse> getAllStudentRequestOfCourse(String courseId, int pageNumber, int pageSize){
		
		int page = Objects.isNull(pageNumber) || pageNumber < 0 ? 0 : pageNumber;
	    int size = Objects.isNull(pageSize) || pageSize <= 0 ? 20 : pageSize;
	    
	    Pageable pageable = PageRequest.of(page, size);
	    
		Page<Student> students = joinClassRequestRepository.findAllStudentsByCourseIdAndStatus(courseId,JoinClassStatus.PENDING.name(),pageable);
		return students.map(studentMapper::tosStudentOfCourseResponse);
	}
	
	public Page<CourseViewResponse> getAllCourseRequestOfStudent(String studentId,int pageNumber, int pageSize){
		
		int page = Objects.isNull(pageNumber) || pageNumber < 0 ? 0 : pageNumber;
	    int size = Objects.isNull(pageSize) || pageSize <= 0 ? 20 : pageSize;
	    
	    Pageable pageable = PageRequest.of(page, size);
		
		Page<Course> courses = joinClassRequestRepository.findAllCoursesByStudentIdAndStatus(studentId, JoinClassStatus.PENDING.name(),pageable);
		Page<CourseViewResponse> courseResponses = courses.map(course -> {
	        CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
	        cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
	        cr.setLessonCount(lessonRepository.countLessonsByCourse(course));
	        return cr;
	    });
		return courseResponses;	
	} 
}
