package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.StudentLessonProgressResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentLessonProgress;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.StudentLessonProgressMapper;
import com.husc.lms.repository.AccountRepository;

import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentLessonProgressService {

	@Autowired
	private StudentLessonProgressRepository studentLessonProgressRepository;
	@Autowired
	private StudentLessonProgressMapper lessonProgressMapper;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private LessonRepository lessonRepository;
	
	public StudentLessonProgressResponse saveProgressLesson(String lessonId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
		Lesson lesson = lessonRepository.findById(lessonId).get();
		StudentLessonProgress slp = StudentLessonProgress.builder()
				.lesson(lesson)
				.student(student)
				.isCompleted(false)
				.createdBy(name)
				.createdDate(new Date())
				.build();
		slp = studentLessonProgressRepository.save(slp);
		
		return lessonProgressMapper.toStudentLessonProgressResponse(slp);
	}
	

	public StudentLessonProgressResponse setCompletedLesson(String lessonId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
		Lesson lesson = lessonRepository.findById(lessonId).get();
		StudentLessonProgress slp = studentLessonProgressRepository.findByLessonAndStudent(lesson,student);
		slp.setIsCompleted(true);
		slp.setCompleteAt(new Date());
		slp.setLastModifiedBy(name);
		slp.setLastModifiedDate(new Date());
		slp = studentLessonProgressRepository.save(slp);
		return lessonProgressMapper.toStudentLessonProgressResponse(slp);
				
	}
	
	public void deleteLessonProgress(List<StudentLessonProgress> slpList) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		for(StudentLessonProgress slp : slpList) {
			slp.setDeletedBy(account.getEmail());
			slp.setDeletedDate(new Date());
			studentLessonProgressRepository.save(slp);
		}
	}

	public StudentLessonProgressResponse getLessonProgress(String lessonId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));;
		Lesson lesson = lessonRepository.findById(lessonId).get();
		StudentLessonProgress slp = studentLessonProgressRepository.findByLessonAndStudent(lesson,student);
		if(slp == null) {
			return StudentLessonProgressResponse.builder()
					.isCompleted(null)
					.build();
		}
		return lessonProgressMapper.toStudentLessonProgressResponse(slp);
	}
}
