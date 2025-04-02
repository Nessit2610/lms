package com.husc.lms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.SubjectResponse;
import com.husc.lms.mapper.SubjectMapper;
import com.husc.lms.repository.SubjectRepository;

@Service
public class SubjectService {

	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private SubjectMapper subjectMapper;
	
	public List<SubjectResponse> getAllSubject(){
		return subjectRepository.findAll().stream().map(subjectMapper::toSubjectResponse).toList();
	}
	
	
}
