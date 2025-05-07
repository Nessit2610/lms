package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.StudentGroupRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.dto.response.StudentViewResponse;
import com.husc.lms.service.StudentGroupService;

@RestController
@RequestMapping("/studentgroup")
public class StudentGroupController {

	@Autowired
	private StudentGroupService studentGroupService;
	
	@PostMapping("/addstudent")
	public APIResponse<Boolean> addListStudentToGroup(@RequestBody StudentGroupRequest request) {
		return APIResponse.<Boolean>builder()
				.result(studentGroupService.addListStudentToGroup(request))
				.build();
	}
	
	@DeleteMapping("/delete")
	public APIResponse<Boolean> deleteStudentOfGroup(@RequestParam("groupId") String groupId,@RequestParam("studentId") String studentId) {
		return APIResponse.<Boolean>builder()
				.result(studentGroupService.deleteStudentOfGroup(groupId, studentId))
				.build();
	}
	
	@GetMapping("/getgroup")
	public APIResponse<Page<GroupViewResponse>> getGroupsOfStudent(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																	@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return APIResponse.<Page<GroupViewResponse>>builder()
				.result(studentGroupService.getGroupsOfStudent(pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/getstudent")
	public APIResponse<Page<StudentViewResponse>> getStudentsOfGroup(@RequestParam("groupId") String groupId ,
																		@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																		@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentGroupService.getStudentsOfGroup(groupId, pageNumber, pageSize))
				.build();
	}
	@GetMapping("/seachstudent")
	public APIResponse<Page<StudentViewResponse>> seachStudentsInGroup(@RequestParam("groupId") String groupId ,
																			@RequestParam("keyword") String keyword,
																			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																			@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
		return APIResponse.<Page<StudentViewResponse>>builder()
				.result(studentGroupService.searchStudentsInGroup(groupId, keyword, pageNumber, pageSize))
				.build();
	}
	
	
}
