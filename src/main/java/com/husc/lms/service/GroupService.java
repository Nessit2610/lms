package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.GroupRequest;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Teacher;
import com.husc.lms.mapper.GroupMapper;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.TeacherRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private GroupMapper groupMapper;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	public GroupViewResponse createGroup(GroupRequest request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		
		Group group = groupMapper.toGroup(request);
			  group.setTeacher(teacher);
			  group.setCreatedAt(new Date());
		group =groupRepository.save(group);
		
		return groupMapper.toGroupViewResponse(group);
		
	}
	
	
}
