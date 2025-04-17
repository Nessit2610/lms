package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.JoinClassStatus;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.JoinClassRequestRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;

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
	
	public boolean pendingRequest(String courseId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsername(name).get();
		Student student = studentRepository.findByAccount(account);
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		
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
	
	public List<StudentOfCourseResponse> getAllStudentRequestOfCourse(String courseId){
		List<Student> students = joinClassRequestRepository.findAllStudentsByCourseId(courseId);
		return students.stream().map(studentMapper::tosStudentOfCourseResponse).toList();
	}
	
	public List<CourseViewResponse> getAllCourseRequestOfStudent(String studentId){
		List<Course> courses = joinClassRequestRepository.findAllCoursesByStudentId(studentId);
		List<CourseViewResponse> courseResponses = new ArrayList<CourseViewResponse>();
		for(Course c : courses) {
			CourseViewResponse cr = courseMapper.toCourseViewResponse(c);
			cr.setStudentCount(studentCourseRepository.countStudentsByCourse(c));
			cr.setLessonCount(lessonRepository.countLessonsByCourse(c));
			courseResponses.add(cr);
		}
		return courseResponses;	
	} 
}
