package com.husc.lms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TestQuestionRequest;
import com.husc.lms.dto.response.TestQuestionResponse;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.QuestionEnum;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.TestQuestionMapper;
import com.husc.lms.repository.TestInGroupRepository;
import com.husc.lms.repository.TestQuestionRepository;

@Service
public class TestQuestionService {

	@Autowired
	private TestQuestionRepository testQuestionRepository;
	
	@Autowired
	private TestInGroupRepository testInGroupRepository;
	
	@Autowired
	private TestQuestionMapper testQuestionMapper;

	
	public List<TestQuestionResponse> addMoreTestQuestion(String testId,List<TestQuestionRequest> requests) {
		TestInGroup test = testInGroupRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		List<TestQuestion> listQuestions = new ArrayList<TestQuestion>();
		for(TestQuestionRequest testQuestionRequest : requests) {
			TestQuestion t = testQuestionMapper.toTestQuestion(testQuestionRequest);
						 t.setTestInGroup(test);
			t = testQuestionRepository.save(t);
			listQuestions.add(t);
		}
		
		return listQuestions.stream().map(testQuestionMapper::toTestQuestionResponse).toList();
	}
	
	public List<TestQuestion> createTestQuestion(TestInGroup test,List<TestQuestionRequest> requests) {
		List<TestQuestion> listQuestions = new ArrayList<TestQuestion>();
		for(TestQuestionRequest testQuestionRequest : requests) {
			String type = switch (testQuestionRequest.getType()) {
	        case "SINGLE_CHOICE" -> QuestionEnum.SINGLE_CHOICE.name();
	        case "MULTIPLE_CHOICE" -> QuestionEnum.MULTIPLE_CHOICE.name();
	        default -> throw new AppException(ErrorCode.NOT_ALLOWED_TYPE);
			};
			TestQuestion t = testQuestionMapper.toTestQuestion(testQuestionRequest);
						t.setType(type);
						t.setTestInGroup(test);
			listQuestions.add(t);
		}
		return listQuestions;
	}
	
	public boolean deleteQuestionById(String id) {
		TestQuestion testQuestion = testQuestionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
		if(testQuestion != null) {
			testQuestionRepository.delete(testQuestion);
			return true;
		}
		return false;
	}
	
	public void deleteQuestionByTest(TestInGroup testInGroup) {
		List<TestQuestion> listQuestions = testQuestionRepository.findByTestInGroup(testInGroup);
		if(listQuestions != null && !listQuestions.isEmpty()) {
			for(TestQuestion tq : listQuestions) {
				testQuestionRepository.delete(tq);
			}
		}
	}
	
	
}
