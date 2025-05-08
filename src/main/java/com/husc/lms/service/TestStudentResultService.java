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
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));
		Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
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
	    
	    // Tìm tài khoản và sinh viên
	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name)
	        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));  // Thêm ngoại lệ cho tài khoản không tồn tại
	    Student student = studentRepository.findByAccount(account).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));  // Thêm ngoại lệ cho sinh viên không tồn tại
	    
	    // Lấy thông tin bài kiểm tra
	    TestInGroup testInGroup = testInGroupRepository.findById(submitTestRequets.getTestId())
	        .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
	    
	    // Lấy kết quả bài kiểm tra của sinh viên
	    TestStudentResult testStudentResult = testStudentResultRepository.findByStudentAndTestInGroup(student, testInGroup)
	        .orElseThrow(() -> new AppException(ErrorCode.RESULT_NOT_FOUND));  // Thêm ngoại lệ nếu không tìm thấy kết quả kiểm tra
	    
	    // Lấy danh sách câu hỏi của bài kiểm tra (tối ưu việc gọi nhiều lần)
	    List<TestQuestion> testQuestions = testQuestionRepository.findByTestInGroup(testInGroup);

	    // Tạo danh sách câu trả lời
	    int correctCount = 0;
	    int totalScore = 0;

	    for (AnswerRequest request : submitTestRequets.getAnswerRequests()) {
	        
	        TestQuestion testQuestion = testQuestionRepository.findById(request.getQuestionId()).orElseThrow(()-> new AppException(ErrorCode.QUESTION_NOT_FOUND));
	        
	        boolean isCorrect = request.getAnswer().equals(testQuestion.getCorrectAnswers());
	        
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

	    
	    testStudentResult.setSubmittedAt(new Date());
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
}
