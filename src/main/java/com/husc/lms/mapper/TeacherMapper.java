package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.TeacherRequest;
import com.husc.lms.dto.response.TeacherInfoResponse;
import com.husc.lms.dto.response.TeacherResponse;
import com.husc.lms.entity.Teacher;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

	public Teacher toTeacher(TeacherRequest request);
	
	public TeacherResponse toTeacherResponse(Teacher teacher);
	
	public TeacherInfoResponse toTeacherInfoResponse(Teacher teacher);
	
}
