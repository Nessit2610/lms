package com.husc.lms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestQuestionRequest {
	
	@NotNull(message = "NOT_NULL")
	private int point;
	@NotNull(message = "NOT_NULL")
	private String content;
	@NotNull(message = "NOT_NULL")
	private String type;
	@NotNull(message = "NOT_NULL")
	private String options;
	@NotNull(message = "NOT_NULL")
	private String correctAnswers;
	
}
