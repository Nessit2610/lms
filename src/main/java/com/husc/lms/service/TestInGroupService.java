package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TestInGroupRequest;
import com.husc.lms.dto.request.TestQuestionRequest;
import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.TestInGroupMapper;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.TestInGroupRepository;

@Service
public class TestInGroupService {

	@Autowired
	private TestInGroupRepository testInGroupRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private TestQuestionService testQuestionService;
	
	@Autowired
	private TestInGroupMapper testInGroupMapper;
	
	public TestInGroupResponse createTestInGroup(TestInGroupRequest request) {
		Group group = groupRepository.findById(request.getGroupId()).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		TestInGroup testInGroup = TestInGroup.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.group(group)
				.createdAt(new Date())
				.expiredAt(request.getExpiredAt())
				.build();
		testInGroup = testInGroupRepository.save(testInGroup);
		testQuestionService.createTestQuestion(testInGroup, request.getListQuestionRequest());
		
		return testInGroupMapper.toTestInGroupResponse(testInGroup);
		
	}
	
}
