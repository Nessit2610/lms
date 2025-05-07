package com.husc.lms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.husc.lms.entity.Student;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestStudentAnswer;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.TestInGroupRepository;
import com.husc.lms.repository.TestStudentResultRepository;

public class TestStudentResultService {

	@Autowired
	private TestStudentResultRepository testStudentResultRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private TestInGroupRepository testInGroupRepository;
	
	
	
	public Boolean submitTest(String studentId, String testId, int totalCorrect , double score, List<TestStudentAnswer> lisAnswers) {
		
		Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

		
		
		
		
		return false;
	}
}
