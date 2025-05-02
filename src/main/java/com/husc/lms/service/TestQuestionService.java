package com.husc.lms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TestQuestionRequest;
import com.husc.lms.dto.response.TestQuestionResponse;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
import com.husc.lms.mapper.TestQuestionMapper;
import com.husc.lms.repository.TestQuestionRepository;

@Service
public class TestQuestionService {

	@Autowired
	private TestQuestionRepository testQuestionRepository;
	
	@Autowired
	private TestQuestionMapper testQuestionMapper;

	
//	public List<TestQuestionResponse> createTestQuestion(TestInGroup test,List<TestQuestionRequest> requests) {
//		List<TestQuestion> listQuestions = new ArrayList<TestQuestion>();
//		for(TestQuestionRequest testQuestionRequest : requests) {
//			TestQuestion t = testQuestionMapper.toTestQuestion(testQuestionRequest);
//						 t.setTest(test);
//			t = testQuestionRepository.save(t);
//			listQuestions.add(t);
//		}
//		
//		return listQuestions.stream().map(testQuestionMapper::toTestQuestionResponse).toList();
//	}
	public void createTestQuestion(TestInGroup test,List<TestQuestionRequest> requests) {
		List<TestQuestion> listQuestions = new ArrayList<TestQuestion>();
		for(TestQuestionRequest testQuestionRequest : requests) {
			TestQuestion t = testQuestionMapper.toTestQuestion(testQuestionRequest);
			t.setTest(test);
			t = testQuestionRepository.save(t);
			listQuestions.add(t);
		}
	}
	
}
