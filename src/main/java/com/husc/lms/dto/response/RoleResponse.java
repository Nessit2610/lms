package com.husc.lms.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
	private String id;
	private String name;
	private String description;
	private Set<PermissionResponse> permissions;
}
