package com.husc.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentCourseRequest;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentCourseService {

	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private StudentMapper studentMapper;
	
	public void addListStudentToCourse(StudentCourseRequest request) {
		Course course = courseRepository.findById(request.getCourseId()).get();
		for(String id : request.getStudentIds()) {
			Student student = studentRepository.findById(id).get();
			StudentCourse studentCourse = StudentCourse.builder()
					.registrationDate(new Date())
					.createdDate(new Date())
					.student(student)
					.course(course)
					.build();
			studentCourseRepository.save(studentCourse);
		}
	}
	
	public void addStudentToCourse(Student student , Course course) {
		StudentCourse studentCourse = StudentCourse.builder()
				.registrationDate(new Date())
				.createdDate(new Date())
				.student(student)
				.course(course)
				.build();
		studentCourseRepository.save(studentCourse);
		
	}
	
	
	public List<CourseViewResponse> getAllCourseOfStudent() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsername(name).get();
		Student student = studentRepository.findByAccount(account);
		List<Course> courses = courseRepository.findByStudent(student);
		List<CourseViewResponse> courseResponses = new ArrayList<CourseViewResponse>();
		for(Course c : courses) {
			CourseViewResponse cr = courseMapper.toCourseViewResponse(c);
			cr.setStudentCount(studentCourseRepository.countStudentsByCourse(c));
			cr.setLessonCount(lessonRepository.countLessonsByCourse(c));
			courseResponses.add(cr);
		}
		return courseResponses;	}
	
	public List<StudentOfCourseResponse> getAllStudentOfCourse(String courseId){
		Course course = courseRepository.findById(courseId).get();
		List<Student> listSoC = studentRepository.findByCourse(course);
		return listSoC.stream().map(studentMapper::tosStudentOfCourseResponse).toList();
	}
	
	public List<StudentOfCourseResponse> getAllStudentNotInCourse(String courseId){
		Course course = courseRepository.findById(courseId).get();
		List<Student> listSoC = studentRepository.findStudentsNotInCourse(course);
		return listSoC.stream().map(studentMapper::tosStudentOfCourseResponse).toList();
	}
	
}
