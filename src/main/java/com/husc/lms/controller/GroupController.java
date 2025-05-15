package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.GroupRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.dto.update.GroupUpdate;
import com.husc.lms.service.GroupService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/group")
public class GroupController {
	
	@Autowired
	private GroupService groupService;
	
	@PostMapping("/create")
	public APIResponse<GroupViewResponse> createGroup(@RequestBody GroupRequest request){
		return APIResponse.<GroupViewResponse>builder()
				.result(groupService.createGroup(request))
				.build();
	}
	
	@PutMapping("/update")
	public APIResponse<GroupViewResponse> updateGroup(@RequestBody GroupUpdate request){
		return APIResponse.<GroupViewResponse>builder()
				.result(groupService.updateGroup(request))
				.build();
	}
	
	@GetMapping("/groupofteacher")
	public APIResponse<Page<GroupViewResponse>> groupOfTeacher(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
														@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<GroupViewResponse>>builder()
				.result(groupService.getGroupOfTeacher(pageNumber, pageSize))
				.build();
	}
	
	@GetMapping("/searchgroupofteacher")
	public APIResponse<Page<GroupViewResponse>> searchGroupOfTeacher(@RequestParam("groupName") String groupName,
																	@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
																	@RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize){
		return APIResponse.<Page<GroupViewResponse>>builder()
				.result(groupService.searchGroupOfTeacher(groupName, pageNumber, pageSize))
				.build();
	}
}
