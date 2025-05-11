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
public class LessonUpdateRequest {

	@NotNull(message = "NOT_NULL")
	private String idLesson;
	
	private String description;
	
	@NotNull(message = "NOT_NULL")
	private Integer order;
}
