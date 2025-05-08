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

	    // Kiểm tra nếu đã bắt đầu
	    if (testStudentResultRepository.findByStudentAndTest(student, testInGroup) != null) {
	        throw new AppException(ErrorCode.TEST_ALREADY_STARTED);
	    }

	    TestStudentResult testStudentResult = TestStudentResult.builder()
	            .student(student)
	            .test(testInGroup)
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
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTest(student, testInGroup);
		List<TestStudentAnswer> list = new ArrayList<TestStudentAnswer>();
		int correctCount = 0;
		int totalScore = 0;
		for(AnswerRequest request : submitTestRequets.getAnswerRequests()) {
			TestQuestion testQuestion = testQuestionRepository.findById(request.getQuestionId()).orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
			boolean iscorrect = false;
			if(request.getAnswer().equals(testQuestion.getCorrectAnswers())) {
				iscorrect = true;
				correctCount++;
				totalScore+= testQuestion.getPoint();
			}
			TestStudentAnswer testStudentAnswer = TestStudentAnswer.builder()
					.answer(request.getAnswer())
					.result(testStudentResult)
					.question(testQuestion)
					.isCorrect(iscorrect)
					.build();
			list.add(testStudentAnswer);
		}
		
		testStudentResult.setAnswers(list);
		testStudentResult.setScore(totalScore);
		testStudentResult.setTotalCorrect(correctCount);
		testStudentResult = testStudentResultRepository.save(testStudentResult);
		return true;
	}
	
	public TestStudentResultResponse getDetail(String testId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account);
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTest(student, testInGroup);
	
		return testStudentResultMapper.toTestStudentResultResponse(testStudentResult);
	}
}
