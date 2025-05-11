package com.husc.lms.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRequest {
	
	@NotNull(message = "NOT_NULL")
	private String name;
	
	private String description;
	private Set<String> permissions;
}