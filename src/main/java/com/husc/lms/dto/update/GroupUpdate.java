package com.husc.lms.dto.update;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupUpdate {

	@NotNull(message = "NOT_NULL")
	private String groupId;
	
	@NotNull(message = "NOT_NULL")
	private String name;
	
	private String description;
	
}
