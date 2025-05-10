package com.husc.lms.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
	    TestInGroup testInGroup = testInGroupRepository
	        .findById(testId)
	        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

	    
	    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);  // Lấy thời gian hiện tại theo UTC
	    OffsetDateTime startedAt = testInGroup.getStartedAt().withOffsetSameInstant(ZoneOffset.UTC);  // Chuyển đổi thời gian bắt đầu về UTC nếu nó không phải UTC
	    OffsetDateTime expiredAt = testInGroup.getExpiredAt().withOffsetSameInstant(ZoneOffset.UTC);  // Chuyển đổi thời gian hết hạn về UTC nếu nó không phải UTC

	    // Kiểm tra nếu thời gian hiện tại trước thời gian bắt đầu
	    if (now.isBefore(startedAt)) {
	        throw new AppException(ErrorCode.TEST_NOT_STARTED_YET);
	    }

	    // Kiểm tra nếu thời gian hiện tại sau thời gian hết hạn
	    if (now.isAfter(expiredAt)) {
	        throw new AppException(ErrorCode.TEST_IS_EXPIRED);
	    }



	    boolean alreadyStarted = !testStudentResultRepository
	        .findByStudentAndTestInGroup(student, testInGroup)
	        .isEmpty();

	    if (alreadyStarted) {
	        throw new AppException(ErrorCode.TEST_ALREADY_STARTED);
	    }


	    TestStudentResult testStudentResult = TestStudentResult.builder()
	            .student(student)
	            .testInGroup(testInGroup)
	            .startedAt(OffsetDateTime.now(ZoneOffset.UTC))
	            .build();

	    testStudentResultRepository.save(testStudentResult);
	    return true;
	}
	
	
	public boolean submitTest(SubmitTestRequets submitTestRequets) {
	    var context = SecurityContextHolder.getContext();
	    String name = context.getAuthentication().getName();
	    
	    
	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name)
	        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND)); 
	    Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));  
	   
	    TestInGroup testInGroup = testInGroupRepository.findById(submitTestRequets.getTestId())
	        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
	    
	    
	    TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup)
	        .orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND)); 

	    int correctCount = 0;
	    int totalScore = 0;

	    for (AnswerRequest request : submitTestRequets.getAnswerRequests()) {
	        
	        TestQuestion testQuestion = testQuestionRepository.findById(request.getQuestionId()).orElseThrow(()-> new AppException(ErrorCode.QUESTION_NOT_FOUND));
	        
	        boolean isCorrect = normalizeAnswer(request.getAnswer()).equals(normalizeAnswer(testQuestion.getCorrectAnswers()));
	        System.out.println("Request answer set: " + normalizeAnswer(request.getAnswer()));
	        System.out.println("Correct answer set: " + normalizeAnswer(testQuestion.getCorrectAnswers()));

	        if (isCorrect) {
	            correctCount++;
	            totalScore += testQuestion.getPoint();
	        }
	        
	        TestStudentAnswer testStudentAnswer = TestStudentAnswer.builder()
	            .answer(request.getAnswer())
	            .testStudentResult(testStudentResult)
	            .testQuestion(testQuestion)
	            .correct(isCorrect)
	            .build();
	        
	        testStudentAnswerRepository.save(testStudentAnswer);
	    }

	    
	    testStudentResult.setSubmittedAt(OffsetDateTime.now(ZoneOffset.UTC));
	    testStudentResult.setScore(totalScore);
	    testStudentResult.setTotalCorrect(correctCount);
	    
	    
	    testStudentResultRepository.save(testStudentResult);

	    return true;
	}

	
	public TestStudentResultResponse getDetail(String testId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
	
		return testStudentResultMapper.toTestStudentResultResponse(testStudentResult);
	}
	
	private Set<String> normalizeAnswer(String answer) {
	    return Arrays.stream(answer.split(","))
	        .map(String::trim)           
	        .map(String::toUpperCase)    
	        .collect(Collectors.toSet()); 
	}
}
