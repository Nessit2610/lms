package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.TestInGroupRequest;
import com.husc.lms.dto.response.TestInGroupResponse;
import com.husc.lms.dto.response.TestInGroupViewResponse;
import com.husc.lms.dto.update.TestInGroupUpdateRequest;
import com.husc.lms.entity.Group;
import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;
import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.exception.AppException;
import com.husc.lms.mapper.TestInGroupMapper;
import com.husc.lms.repository.GroupRepository;
import com.husc.lms.repository.TestInGroupRepository;
import com.husc.lms.repository.TestQuestionRepository;
import com.husc.lms.repository.TestStudentResultRepository;

import jakarta.transaction.Transactional;

@Service
public class TestInGroupService {

	@Autowired
	private TestInGroupRepository testInGroupRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private TestQuestionService testQuestionService;
	
	@Autowired
	private TestStudentResultService testStudentResultService;
	
	@Autowired
	private TestStudentResultRepository testStudentResultRepository;
	
	@Autowired
	private TestInGroupMapper testInGroupMapper;
	
	public TestInGroupResponse createTestInGroup(TestInGroupRequest request) {
		Group group = groupRepository.findById(request.getGroupId()).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		
		OffsetDateTime startedAtUtc = request.getStartedAt().withOffsetSameInstant(ZoneOffset.UTC);
		OffsetDateTime expiredAtUtc = request.getExpiredAt().withOffsetSameInstant(ZoneOffset.UTC);

	    TestInGroup testInGroup = TestInGroup.builder()
	            .title(request.getTitle())
	            .description(request.getDescription())
	            .group(group)
	            .startedAt(startedAtUtc)  
	            .createdAt(OffsetDateTime.now(ZoneOffset.UTC))  
	            .expiredAt(expiredAtUtc)  
	            .build();
		List<TestQuestion> listQuestions = testQuestionService.createTestQuestion(testInGroup, request.getListQuestionRequest());
		testInGroup.setQuestions(listQuestions);
		testInGroup = testInGroupRepository.save(testInGroup);
		return testInGroupMapper.toTestInGroupResponse(testInGroup);
		
	}
	
	@Transactional
	public TestInGroupResponse updateTestInGroup(TestInGroupUpdateRequest request) {
	    OffsetDateTime startedAtUtc = request.getStartedAt().withOffsetSameInstant(ZoneOffset.UTC);
	    OffsetDateTime expiredAtUtc = request.getExpiredAt().withOffsetSameInstant(ZoneOffset.UTC);

	    TestInGroup testInGroup = testInGroupRepository.findById(request.getTestInGroupId())
	            .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

	    testInGroup.setTitle(request.getTitle());
	    testInGroup.setDescription(request.getDescription());
	    testInGroup.setStartedAt(startedAtUtc);
	    testInGroup.setExpiredAt(expiredAtUtc);
	    
	    List<TestStudentResult> listTestStudentResults = testStudentResultRepository.findByTestInGroup(testInGroup);
	    if(listTestStudentResults == null) {
	    	 // Tạo hoặc cập nhật danh sách câu hỏi mới
		    List<TestQuestion> updatedQuestions = testQuestionService.createTestQuestion(testInGroup, request.getListQuestionRequest());

		    // Cập nhật collection hiện tại tránh gán list mới
		    List<TestQuestion> currentQuestions = testInGroup.getQuestions();
		    currentQuestions.clear();
		    currentQuestions.addAll(updatedQuestions);

	    }
	    testInGroup = testInGroupRepository.save(testInGroup);

	    return testInGroupMapper.toTestInGroupResponse(testInGroup);
	}


	
	public TestInGroupResponse getById(String id) {
		TestInGroup testInGroup = testInGroupRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		return testInGroupMapper.toTestInGroupResponse(testInGroup);
	}
	
	public Page<TestInGroupViewResponse> getAllTestInGroup(String groupId, int pageNumber , int pageSize) {
		 Pageable pageable = PageRequest.of(pageNumber, pageSize,Sort.by(Sort.Direction.DESC, "createdAt"));
		 Group group = groupRepository.findById(groupId).orElseThrow(()-> new AppException(ErrorCode.GROUP_NOT_FOUND));
		 Page<TestInGroup> testInGroup = testInGroupRepository.findByGroup(group, pageable);
		 return testInGroup.map(testInGroupMapper::toTestInGroupViewResponse);
	}
	
	public boolean deleteTestInGroup(String id) {
		TestInGroup testInGroup = testInGroupRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
		testStudentResultService.deleteTestResult(testInGroup);
		testInGroupRepository.delete(testInGroup);
		return true;
	}
	
}
