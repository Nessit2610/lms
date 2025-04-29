package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentGroupRequest;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentGroup;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.StudentGroupRepository;
import com.husc.lms.repository.StudentRepository;

@Service
public class StudentGroupService {

	
	@Autowired 
	private StudentGroupRepository studentGroupRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	public Boolean addListStudentToGroup(StudentGroupRequest request) {
		Group group = groupRepository.findById(request.getGroupId()).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		for(String id : request.getStudentIds()) {
			Student student = studentRepository.findById(id).get();
			StudentGroup studentGroup = StudentGroup.builder()
					.group(group)
					.student(student)
					.joinAt(new Date())
					.build();
			studentGroupRepository.save(studentGroup);
			return true;
		}
		return false;
	}
}
