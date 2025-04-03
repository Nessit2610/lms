package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.StudentRequest;
import com.husc.lms.dto.response.LearningPlanOfStudent;
import com.husc.lms.dto.response.StudentOfClassResponse;
import com.husc.lms.dto.response.StudentResponse;
import com.husc.lms.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

	public Student toStudent(StudentRequest request);
	
	public StudentResponse toStudentResponse(Student student);
	
	public StudentOfClassResponse toStudentOfClassResponse(Student student);
	
	public LearningPlanOfStudent toLearningPlanOfStudent(Student student);
}
