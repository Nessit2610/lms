package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentCourseRequest;
import com.husc.lms.dto.request.StudentDeleteRequest;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.JoinClassStatus;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.CourseMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.JoinClassRequestRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentCourseService {

	@Autowired
	private JoinClassRequestRepository joinClassRequestRepository;
	
	@Autowired
	private StudentCourseRepository studentCourseRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private ChapterRepository chapterRepository;
	
	@Autowired
	private CourseMapper courseMapper;
	
	@Autowired
	private StudentMapper studentMapper;
	
	public boolean addListStudentToCourse(StudentCourseRequest request) {
		Course course = courseRepository.findById(request.getCourseId()).get();
		for(String id : request.getStudentIds()) {
			Student student = studentRepository.findById(id).get();
			addStudentToCourse(student, course);
		}
		return true;
	}
	
	public void addStudentToCourse(Student student , Course course) {
		
		if (course.getEndDate() != null) {
		    Date now = new Date();
		    if (course.getEndDate().before(now)) {
		        throw new AppException(ErrorCode.COURSE_ENDED);
		    }
		}

		if (course.getStartDate() != null) {
		    Date now = new Date();
		    if (course.getStartDate().after(now)) {
		        throw new AppException(ErrorCode.COURSE_NOT_STARTED);
		    }
		}
		
		
		JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
		if(joinClassRequest != null) {
			joinClassRequest.setStatus(JoinClassStatus.APPROVED.name());
			joinClassRequestRepository.save(joinClassRequest);
		}
		if(studentCourseRepository.existsByStudentAndCourseAndDeletedDateIsNull(student, course)) {
			throw new AppException(ErrorCode.STUDENT_ALREADY_IN_COURSE);
		}
		else if(studentCourseRepository.existsByStudentAndCourseAndDeletedDateIsNotNull(student, course)) {
			StudentCourse studentCourseExisted =studentCourseRepository.findByCourseAndStudentAndDeletedDateIsNotNull(course, student);
			studentCourseExisted.setDeletedBy(null);
			studentCourseExisted.setDeletedDate(null);
			studentCourseRepository.save(studentCourseExisted);
			
		}
		else {
			StudentCourse studentCourse = StudentCourse.builder()
					.registrationDate(new Date())
					.createdDate(new Date())
					.student(student)
					.course(course)
					.build();
			studentCourseRepository.save(studentCourse);
			
		}
	}
	public void addStudentBuyCourse(Student student , Course course) {
		StudentCourse studentCourse = StudentCourse.builder()
				.registrationDate(new Date())
				.createdDate(new Date())
				.student(student)
				.course(course)
				.build();
		studentCourseRepository.save(studentCourse);
	}
	
	public boolean deleteStudentOfCourse(String studentId, String courseId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);
		if (course == null) {
			throw new AppException(ErrorCode.COURSE_NOT_FOUND);
		}
		Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		if(course != null && student != null) {
			if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.APPROVED.name())) {
				JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
				joinClassRequest.setStatus(JoinClassStatus.REJECTED.name());
				joinClassRequestRepository.save(joinClassRequest);
			}
			StudentCourse studentCourse = studentCourseRepository.findByCourseAndStudentAndDeletedDateIsNull(course, student);
			if(studentCourse != null) {
				studentCourse.setDeletedBy(account.getEmail());
				studentCourse.setDeletedDate(new Date());
				studentCourse = studentCourseRepository.save(studentCourse);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean deleteListStudentOfCourse(StudentDeleteRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Course course = courseRepository.findByIdAndDeletedDateIsNull(request.getBaseId());
		if (course == null) {
			throw new AppException(ErrorCode.COURSE_NOT_FOUND);
		}
		for(String studentId : request.getStudentIds()) {
			Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
			if(course != null && student != null) {
				if(joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.APPROVED.name())) {
					JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
					joinClassRequest.setStatus(JoinClassStatus.REJECTED.name());
					joinClassRequestRepository.save(joinClassRequest);
				}
				StudentCourse studentCourse = studentCourseRepository.findByCourseAndStudentAndDeletedDateIsNull(course, student);
				if(studentCourse != null) {
					studentCourse.setDeletedBy(account.getEmail());
					studentCourse.setDeletedDate(new Date());
					studentCourse = studentCourseRepository.save(studentCourse);
				}
			}
		}
		return true;
	}
	
	public boolean deleteListStudentBeforeDeleteCourse(List<StudentCourse> listStudentCourses) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name)
			.orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

		for (StudentCourse studentCourse : listStudentCourses) {
			if (studentCourse.getDeletedDate() == null) {
				studentCourse.setDeletedBy(account.getEmail());
				studentCourse.setDeletedDate(new Date());
				Student student = studentCourse.getStudent();
				Course course = studentCourse.getCourse();

				if (joinClassRequestRepository.existsByStudentAndCourseAndStatus(student, course, JoinClassStatus.APPROVED.name())) {
					JoinClassRequest joinClassRequest = joinClassRequestRepository.findByStudentAndCourse(student, course);
					joinClassRequest.setStatus(JoinClassStatus.REJECTED.name());
					joinClassRequestRepository.save(joinClassRequest);
				}
			}
		}

		studentCourseRepository.saveAll(listStudentCourses);
		return true;
	}

	
	public Page<CourseViewResponse> getAllCourseOfStudent(int page, int size) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
	    
	    Pageable pageable = PageRequest.of(page, size);
	    
		Page<Course> coursePage = studentCourseRepository.findCourseByStudent(student,pageable);
		Page<CourseViewResponse> courseResponsePage = coursePage.map(course -> {
	        CourseViewResponse cr = courseMapper.toCourseViewResponse(course);
	        cr.setStudentCount(studentCourseRepository.countStudentsByCourse(course));
	        cr.setChapterCount(chapterRepository.countChaptersByCourse(course));
	        return cr;
	    });
		
		return courseResponsePage;	
	}
	
	public Page<StudentViewResponse> getAllStudentOfCourse(String courseId, int page, int size){
		
	    Pageable pageable = PageRequest.of(page, size);
	    
		Course course = courseRepository.findById(courseId).get();
		Page<Student> listSoC = studentCourseRepository.findStudentByCourse(course, pageable);
		return listSoC.map(studentMapper::toStudentViewResponse);
	}
	
	public Page<StudentViewResponse> searchStudentInCourse(String courseId,String keyword, int page, int size){
		
		Pageable pageable = PageRequest.of(page, size);
		
		Course course = courseRepository.findById(courseId).get();
		Page<Student> listSoC = studentCourseRepository.searchStudentsInCourse(course, keyword, pageable);
		return listSoC.map(studentMapper::toStudentViewResponse);
	}
	
	public Page<StudentViewResponse> searchStudentNotInCourse(String courseId,String keyword, int page, int size){
		
		Pageable pageable = PageRequest.of(page, size);
		
		Course course = courseRepository.findById(courseId).get();
		Page<Student> listSoC = studentCourseRepository.searchStudentsNotInCourse(course, keyword, pageable);
		return listSoC.map(studentMapper::toStudentViewResponse);
	}
	
	
	
	
}
