package com.husc.lms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.ClassRequest;
import com.husc.lms.dto.response.ClassOfStudentResponse;
import com.husc.lms.dto.response.ClassResponse;
import com.husc.lms.dto.response.StudentOfClassResponse;
import com.husc.lms.dto.response.UserResponse;
import com.husc.lms.entity.Class;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;
import com.husc.lms.entity.User;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.ClassMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.ClassRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.StudentRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class ClassService {

	@Autowired
	private ClassRepository classRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private StudentMapper studentMapper;
	
	private UserService userService;
	
	@Autowired
	private ClassMapper classMapper;
	
	public ClassResponse createClass(ClassRequest request) {
		Course course = courseRepository.findById(request.getCourseId()).orElseThrow(()-> new AppException(ErrorCode.CODE_ERROR));
		Teacher teacher = teacherRepository.findById(request.getTeacherId()).orElseThrow(()-> new AppException(ErrorCode.CODE_ERROR));
		Class c = Class.builder()
				.code(request.getCode())
				.status(request.getStatus())
				.name(request.getName())
				.teacher(teacher)
				.course(course)
				.build();
		c = classRepository.save(c);
		
		return classMapper.toClassResponse(c);
		
	}
	
	public ClassResponse getClass(String id) {
		Class c = classRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CODE_ERROR));
		ClassResponse cre = classMapper.toClassResponse(c);
		List<StudentOfClassResponse> lst = studentRepository.findByClassId(c).stream().map(studentMapper::toStudentOfClassResponse).toList();
		cre.setStudent(lst);
		return cre ;
	}
	
	public ClassResponse addStudentToClass(List<String> studentIds, String classId) {
	  	Class c = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));

		for(String s : studentIds) {
			Student st = studentRepository.findById(s).orElseThrow(() -> new AppException(ErrorCode.CODE_ERROR));
			st.setClassId(c);
			st = studentRepository.save(st);
		}
		ClassResponse cre = classMapper.toClassResponse(c);
		List<StudentOfClassResponse> lst = studentRepository.findByClassId(c).stream().map(studentMapper::toStudentOfClassResponse).toList();
		cre.setStudent(lst);
		return cre ;
		
	}

	
	
//	public List<ClassOfStudentResponse> getAllClassOfStudent(){
//		UserResponse user = userService.getMyInfo();
//		Student s = studentRepository.findByUserId(user.getId());
//		
//	}
//	
	
}
