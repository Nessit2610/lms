package com.husc.lms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.request.AccountRequest;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.dto.response.AccountResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Teacher;
import com.husc.lms.mapper.TeacherMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TeacherMapper teacherMapper;
	
	@Autowired
	private AccountService accountService;
	
	public TeacherResponse createTeacher(TeacherRequest request) {
		
		AccountRequest uRequest = AccountRequest.builder()
				.username(request.getEmail())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		AccountResponse accountResponse = accountService.createAccountTeacher(uRequest);
		Account account = accountRepository.findById(accountResponse.getId()).get();
		
		Teacher teacher = teacherMapper.toTeacher(request);
				teacher.setAccount(account);
		teacher = teacherRepository.save(teacher);
		return teacherMapper.toTeacherResponse(teacher);
	}
}
