package com.husc.lms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Teacher;
import com.husc.lms.repository.TeacherRepository;

@Service
public class TeacherService {

	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private UserService userService;
	
	public TeacherResponse createTeacher(TeacherRequest request) {
		
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserTeacher(uRequest);
		Teacher teacher = Teacher.builder()
				.userId(userResponse.getId())
				.email(request.getEmail())
				.fullName(request.getFullName())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.gender(request.getGender())
				.code("TESTCODE")
				.majorId("1")
				.build();
		
		teacher = teacherRepository.save(teacher);
		TeacherResponse teacherResponse = TeacherResponse.builder()
				.email(teacher.getEmail())
				.fullName(teacher.getFullName())
				.firstName(teacher.getFirstName())
				.lastName(teacher.getLastName())
				.build();
		return teacherResponse;
	}
}
