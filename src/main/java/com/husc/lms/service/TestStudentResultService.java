package com.husc.lms.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.AnswerRequest;
import com.husc.lms.dto.request.SubmitTestRequets;
import com.husc.lms.dto.response.TestStudentResultResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
import com.husc.lms.entity.TestStudentAnswer;
import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.TestStudentResultMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.TestInGroupRepository;
import com.husc.lms.repository.TestQuestionRepository;
import com.husc.lms.repository.TestStudentAnswerRepository;
import com.husc.lms.repository.TestStudentResultRepository;

@Service
public class TestStudentResultService {

	@Autowired
	private TestStudentResultRepository testStudentResultRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private TestInGroupRepository testInGroupRepository;
	
	@Autowired
	private TestQuestionRepository testQuestionRepository;
	
	@Autowired
	private TestStudentAnswerRepository testStudentAnswerRepository;
	
	
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TestStudentResultMapper testStudentResultMapper;
	
	public boolean startTest(String testId) {	
	    String name = SecurityContextHolder.getContext().getAuthentication().getName();

	    Account account = accountRepository
	        .findByUsernameAndDeletedDateIsNull(name)
	        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

	    Student student = studentRepository.findByAccount(account);
	    TestInGroup testInGroup = testInGroupRepository
	        .findById(testId)
	        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

	    Instant now = Instant.now();
	    Instant startedAt = testInGroup.getStartedAt().toInstant();
	    Instant expiredAt = testInGroup.getExpiredAt().toInstant();

	    if (now.isBefore(startedAt)) {
	        throw new AppException(ErrorCode.TEST_NOT_STARTED_YET);
	    }

	    if (now.isAfter(expiredAt)) {
	        throw new AppException(ErrorCode.TEST_IS_EXPIRED);
	    }

	    if (testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup) != null) {
	        throw new AppException(ErrorCode.TEST_ALREADY_STARTED);
	    }

	    TestStudentResult testStudentResult = TestStudentResult.builder()
	            .student(student)
	            .testInGroup(testInGroup)
	            .startedAt(new Date())
	            .build();

	    testStudentResultRepository.save(testStudentResult);
	    return true;
	}
	
	
	public boolean submitTest(SubmitTestRequets submitTestRequets) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account);
		TestInGroup testInGroup = testInGroupRepository.findById(submitTestRequets.getTestId()).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup);
		List<TestStudentAnswer> list = new ArrayList<TestStudentAnswer>();
		int correctCount = 0;
		int totalScore = 0;
		for (AnswerRequest request : submitTestRequets.getAnswerRequests()) {
		    TestQuestion testQuestion = testQuestionRepository.findById(request.getQuestionId())
		        .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
		    
		    boolean isCorrect = request.getAnswer().equals(testQuestion.getCorrectAnswers());
		    if (isCorrect) {
		        correctCount++;
		        totalScore += testQuestion.getPoint();
		    }
		    
		    TestStudentAnswer testStudentAnswer = TestStudentAnswer.builder()
		        .answer(request.getAnswer())
		        .testStudentResult(testStudentResult)
		        .testQuestion(testQuestion)
		        .isCorrect(isCorrect)
		        .build();
		    testStudentAnswer = testStudentAnswerRepository.save(testStudentAnswer);
		    list.add(testStudentAnswer);
		}

		// Cập nhật kết quả và câu trả lời vào TestStudentResult
		testStudentResult.setTestStudentAnswer(list);
		testStudentResult.setScore(totalScore);
		testStudentResult.setTotalCorrect(correctCount);

		// Lưu lại TestStudentResult (Hibernate sẽ tự động lưu các TestStudentAnswer liên quan)
		testStudentResultRepository.save(testStudentResult);

		return true;
	}
	
	public TestStudentResultResponse getDetail(String testId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account);
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup);
	
		return testStudentResultMapper.toTestStudentResultResponse(testStudentResult);
	}
}
