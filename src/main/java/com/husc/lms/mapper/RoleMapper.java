package com.husc.lms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.husc.lms.dto.request.RoleRequest;
import com.husc.lms.dto.response.RoleResponse;
import com.husc.lms.entity.Role;


@Mapper(componentModel = "spring")
public interface RoleMapper {
	
	@Mapping(target = "permission", ignore = true)
	public Role toRole(RoleRequest request);
	
	public RoleResponse toRoleResponse(Role role);
}
