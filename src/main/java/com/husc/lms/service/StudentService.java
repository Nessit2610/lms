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
import com.husc.lms.entity.Major;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.User;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.MajorMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.MajorRepository;
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
	private MajorRepository majorRepository;
	
	@Autowired
	private UserService userService;
	
	public StudentResponse createStudent(StudentRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Major major = majorRepository.findById(request.getIdmajor()).orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
		System.out.println(major.getCode());
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserStudent(uRequest);
		Student student = studentMapper.toStudent(request);
		student.setUserId(userResponse.getId());
		student.setCode("TESTCODE");
		student.setCreatedBy(name);
		student.setMajorId(major);
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
		User user = userRepository.findByUsername(name).get();
		Student student = studentRepository.findByUserId(user.getId());
		return studentMapper.toStudentResponse(student);
		
	}

}
