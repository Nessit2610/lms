package com.husc.lms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.GroupRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.GroupViewResponse;
import com.husc.lms.dto.update.GroupUpdate;
import com.husc.lms.service.GroupService;
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
}
