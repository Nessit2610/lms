package com.husc.lms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.repository.TeacherRepository;

@Service
public class TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;
	
	public TeacherResponse createTeacher(TeacherRequest request) {
		TeacherResponse teacherResponse = new TeacherResponse();
		return teacherResponse;
	}
}
