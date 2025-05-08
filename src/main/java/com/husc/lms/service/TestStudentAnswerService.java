package com.husc.lms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.TestQuestion;
import com.husc.lms.entity.TestStudentAnswer;
import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.repository.TestStudentAnswerRepository;

@Service
public class TestStudentAnswerService {

	@Autowired
	private TestStudentAnswerRepository testStudentAnswerRepository;
	
	
}
