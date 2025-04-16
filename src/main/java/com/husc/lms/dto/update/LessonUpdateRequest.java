package com.husc.lms.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonUpdateRequest {

	private String idLesson;
	private String description;
	private Integer order;
}
