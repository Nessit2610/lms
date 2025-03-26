package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.User;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.UserRepository;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	public StudentResponse createStudent(StudentRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserStudent(uRequest);
		Student student = studentMapper.toStudent(request);
		student.setUserId(userResponse.getId());
		student.setMajorId("1");
		student.setCode("TESTCODE");
		student.setCreatedBy(name);
		student.setCreatedDate(new Date());
		student.setLastModifiedBy(name);
		student.setLastModifiedDate(new Date());
		student = studentRepository.save(student);
		
		return studentMapper.toStudentResponse(student);
	}
	
	public List<StudentResponse> getAllStudent(){
		return studentRepository.findAll().stream().map(studentMapper::toStudentResponse).toList();
	}
	
	public StudentResponse getStudentInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		var user = userRepository.findByUsername(name);
		var student = studentRepository.findByUserId(user.get().getId());
		return studentMapper.toStudentResponse(student);
		
	}

}
