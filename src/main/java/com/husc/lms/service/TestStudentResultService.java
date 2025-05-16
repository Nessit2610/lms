package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.AnswerRequest;
import com.husc.lms.dto.request.SubmitTestRequets;
import com.husc.lms.dto.response.TestResultViewResponse;
import com.husc.lms.dto.response.TestStudentResultResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
import com.husc.lms.entity.TestStudentAnswer;
import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.SubmissionStatus;
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
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
	    TestInGroup testInGroup = testInGroupRepository
	        .findById(testId)
	        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

	    
	  
	    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);  

	   
	    OffsetDateTime startedAt = testInGroup.getStartedAt().withOffsetSameInstant(ZoneOffset.UTC);
	    OffsetDateTime expiredAt = testInGroup.getExpiredAt().withOffsetSameInstant(ZoneOffset.UTC);

	    
	    if (now.isBefore(startedAt)) {
	        throw new AppException(ErrorCode.TEST_NOT_STARTED_YET); 
	    }
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
	            .status(SubmissionStatus.NOT_SUBMITTED.name())
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

	    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
	    OffsetDateTime deadline = testInGroup.getExpiredAt();

	    if (now.isBefore(deadline) || now.isEqual(deadline)) {
	    	testStudentResult.setStatus(SubmissionStatus.SUBMITTED.name());
	    } else {
	    	testStudentResult.setStatus(SubmissionStatus.LATE_SUBMISSION.name());
	    }
	    
	    testStudentResult.setSubmittedAt(now);
	    testStudentResult.setScore(totalScore);
	    testStudentResult.setTotalCorrect(correctCount);
	    testStudentResultRepository.save(testStudentResult);

	    return true;
	}

	
	public TestStudentResultResponse getDetail(String studentId, String testId) {
		Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
	
		return testStudentResultMapper.toTestStudentResultResponse(testStudentResult);
	}
	
	public TestStudentResultResponse getDetailofStudent(String testId) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		
		TestInGroup testInGroup = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup).orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));
		
		return testStudentResultMapper.toTestStudentResultResponse(testStudentResult);
	}
	
	
	public Page<TestResultViewResponse> getAllResult(String testId, int pageNumber , int pageSize){
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		TestInGroup testInGroup = testInGroupRepository.findById(testId)
		        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		    
		Page<TestStudentResult> testResults = testStudentResultRepository.findByTestInGroup(testInGroup, pageable);
		
		return testResults.map(testStudentResultMapper::toTestResultViewResponse);
	}
	
	
	public void deleteTestResult(TestInGroup testInGroup) {
		List<TestStudentResult> testStudentResults = testStudentResultRepository.findByTestInGroup(testInGroup);
		if(testStudentResults != null && !testStudentResults.isEmpty()) {
			for(TestStudentResult t : testStudentResults) {
				testStudentResultRepository.delete(t);
			}
		}
	}
	
	
	private Set<String> normalizeAnswer(String answer) {
	    return Arrays.stream(answer.split(","))
	        .map(String::trim)           
	        .map(String::toUpperCase)    
	        .collect(Collectors.toSet()); 
	}
}
