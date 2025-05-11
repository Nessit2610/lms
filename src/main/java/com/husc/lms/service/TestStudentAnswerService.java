package com.husc.lms.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.repository.TestStudentAnswerRepository;

@Service
public class TestStudentAnswerService {

	@Autowired
	private TestStudentAnswerRepository testStudentAnswerRepository;
	
	
}
