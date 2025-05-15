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

import com.husc.lms.dto.request.RoleRequest;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.RoleResponse;
import com.husc.lms.service.RoleService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	@PostMapping("/create")
	public APIResponse<RoleResponse> create(@RequestBody @Valid RoleRequest request){
		return APIResponse.<RoleResponse>builder()
				.result(roleService.create(request))
				.build();
	}
	
	@PostMapping("/update")
	public APIResponse<RoleResponse> updatePermissionsForRole(@RequestBody @Valid RoleRequest request){
		return APIResponse.<RoleResponse>builder()
				.result(roleService.updatePermissionsForRole(request))
				.build();
	}
	
	@GetMapping
	public APIResponse<List<RoleResponse>> getAll(){
		return APIResponse.<List<RoleResponse>>builder()
				.result(roleService.getAll())
				.build();
	}
	
	@DeleteMapping("{roleName}")
	public APIResponse<Void> Delete(@PathVariable("roleName") String roleName){
		roleService.detele(roleName);
		return APIResponse.<Void>builder().build();
	}
	
	
	
	
	
	
	
	
}
