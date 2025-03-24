package com.husc.lms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.request.UserCreationRequest;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.User;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private UserService userService;
	
	public StudentResponse createStudent(StudentRequest request) {
		
		UserCreationRequest uRequest = UserCreationRequest.builder()
				.username(request.getUsername())
				.password(request.getPassword())
				.email(request.getEmail())
				.build();
		UserResponse userResponse = userService.createUserStudent(uRequest);
		Student student = Student.builder()
				.userId(userResponse.getId())
				.email(request.getEmail())
				.fullName(request.getFullName())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.dateOfBirth(request.getDateOfBirth())
				.placeOfBirth(request.getPlaceOfBirth())
				.gender(request.getGender())
				.majorId("1")
				.code("TESTCODE")
				.build();
		student = studentRepository.save(student);
		StudentResponse studentResponse = StudentResponse.builder()
				.username(uRequest.getUsername())
				.password(uRequest.getPassword())
				.email(student.getEmail())
				.fullName(student.getFullName())
				.firstName(student.getFirstName())
				.lastName(student.getLastName())
				.dateOfBirth(student.getDateOfBirth())
				.placeOfBirth(student.getPlaceOfBirth())
				.gender(student.getGender())
				.build();
		return studentResponse;
	}

}
