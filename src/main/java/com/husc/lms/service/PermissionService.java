package com.husc.lms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.PermissionRequest;
import com.husc.lms.dto.response.PermissionResponse;
import com.husc.lms.entity.Permission;
import com.husc.lms.mapper.PermissionMapper;
import com.husc.lms.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private PermissionMapper permissionMapper;
	
	public PermissionResponse create(PermissionRequest request) {
		Permission permission = permissionMapper.toPermission(request);
		permission = permissionRepository.save(permission);
		return permissionMapper.toPermissionResponse(permission);
	}
	
	public List<PermissionResponse> getAll(){
		var permissions = permissionRepository.findAll();
		return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
	}
	
	public void delete(String permissionName) {
		permissionRepository.deleteById(permissionName);
	}
}





