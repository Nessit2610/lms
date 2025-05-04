package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.StudentGroupRequest;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.dto.response.StudentOfCourseResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentGroup;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.GroupMapper;
import com.husc.lms.mapper.StudentMapper;
import com.husc.lms.repository.AccountRepository;
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
	private AccountRepository accountRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private GroupMapper groupMapper;
	
	@Autowired
	private StudentMapper studentMapper;
	
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
	
	public Boolean deleteStudentOfGroup(String groupId, String studentId) {
		Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		Student student = studentRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
		StudentGroup studentGroup = studentGroupRepository.findByStudentAndGroup(student, group);
		if(studentGroup != null) {
			studentGroupRepository.delete(studentGroup);
			return true;
		}
		return false;
	}
	
	public Page<GroupViewResponse> getGroupsOfStudent(int page, int size) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));
		Student student = studentRepository.findByAccount(account);
		Pageable pageable = PageRequest.of(page, size);
	    Page<StudentGroup> studentGroups = studentGroupRepository.findByStudent(student, pageable);

	    return studentGroups.map(sg -> {
	        Group g = sg.getGroup();
	        return groupMapper.toGroupViewResponse(g);
	    });
	}
	
	public Page<StudentOfCourseResponse> getStudentsOfGroup(String groupId, int page, int size) {
		Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		Pageable pageable = PageRequest.of(page, size);
	    Page<StudentGroup> studentGroups = studentGroupRepository.findByGroup(group, pageable);

	    return studentGroups.map(sg -> {
	        Student s = sg.getStudent();
	        return studentMapper.toStudentOfCourseResponse(s);
	    });
	}

	public Page<StudentOfCourseResponse> searchStudentsInGroup(String groupId, String keyword, int page, int size) {
		Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
	    Pageable pageable = PageRequest.of(page, size);
	    Page<StudentGroup> studentGroups = studentGroupRepository.searchByFullNameOrEmail(group, keyword, pageable);

	    return studentGroups.map(sg -> {
	        Student s = sg.getStudent();
	        return studentMapper.toStudentOfCourseResponse(s); 
	    });
	}

}
