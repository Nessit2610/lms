package com.husc.lms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.GroupRequest;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.dto.update.GroupUpdate;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.Teacher;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
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
	public GroupViewResponse updateGroup(GroupUpdate request) {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).get();
		Teacher teacher = teacherRepository.findByAccount(account);
		
		Group group = groupRepository.findByIdAndTeacher(request.getGroupId(), teacher);
		if(group == null) {
			throw new AppException(ErrorCode.GROUP_NOT_FOUND);
		}
		
		group.setName(request.getName());
		group.setDescription(request.getDescription());
		group =groupRepository.save(group);
		
		
		return groupMapper.toGroupViewResponse(group);
		
	}
	
	public Page<GroupViewResponse> getGroupOfTeacher(int page, int size){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(name).orElseThrow(()-> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
		Teacher teacher = teacherRepository.findByAccount(account);
		
		Pageable pageable = PageRequest.of(page, size);
		Page<Group> groups = groupRepository.findByTeacher(teacher, pageable);
		
		return groups.map(groupMapper::toGroupViewResponse);
	}
}
