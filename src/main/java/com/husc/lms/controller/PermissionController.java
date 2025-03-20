package com.husc.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.request.PermissionRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.PermissionResponse;
import com.husc.lms.service.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permission")
public class PermissionController {

	@Autowired
	private PermissionService permissionService;

	@PostMapping()
	public APIResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
		return APIResponse.<PermissionResponse>builder()
				.result(permissionService.create(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<PermissionResponse>> getAll(){
		return APIResponse.<List<PermissionResponse>>builder()
				.result(permissionService.getAll())
				.build();
	}
	
	@DeleteMapping("{permissionName}")
	public APIResponse<Void> delete(@PathVariable("permissionName") String permissionName){
		permissionService.delete(permissionName);
		return APIResponse.<Void>builder().build();
	}
	
	
}
