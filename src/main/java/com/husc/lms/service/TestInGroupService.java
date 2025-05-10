package com.husc.lms.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TestInGroupRequest;
import com.husc.lms.dto.request.TestQuestionRequest;
import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.dto.response.TestInGroupViewResponse;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
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
				.startedAt(request.getStartedAt())
				.createdAt(LocalDateTime.now())
				.expiredAt(request.getExpiredAt())
				.build();
		testInGroup = testInGroupRepository.save(testInGroup);
		List<TestQuestion> listQuestions = testQuestionService.createTestQuestion(testInGroup, request.getListQuestionRequest());
		testInGroup.setQuestions(listQuestions);
		testInGroup = testInGroupRepository.save(testInGroup);
		return testInGroupMapper.toTestInGroupResponse(testInGroup);
		
	}
	public TestInGroupResponse getById(String id) {
		TestInGroup testInGroup = testInGroupRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		return testInGroupMapper.toTestInGroupResponse(testInGroup);
	}
	
	public Page<TestInGroupViewResponse> getAllTestInGroup(String groupId, int pageNumber , int pageSize) {
		 Pageable pageable = PageRequest.of(pageNumber, pageSize);
		 Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		 Page<TestInGroup> testInGroup = testInGroupRepository.findByGroup(group, pageable);
		 return testInGroup.map(testInGroupMapper::toTestInGroupViewResponse);
	}
	
	
}
