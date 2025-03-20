package com.husc.lms.mapper;

import org.mapstruct.Mapper;

import com.husc.lms.dto.request.PermissionRequest;
import com.husc.lms.dto.response.PermissionResponse;
import com.husc.lms.entity.Permission;



@Mapper(componentModel = "spring")
public interface PermissionMapper {

	public Permission toPermission(PermissionRequest request);
	public PermissionResponse toPermissionResponse(Permission permission);
}
