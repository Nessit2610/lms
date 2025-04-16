package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.JoinClassStatus;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.JoinClassRequestRepository;
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
	
	public boolean saveRequest(String courseId) {
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
}
