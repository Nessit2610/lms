package com.husc.lms.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitTestRequets {
	
	@NotNull(message = "NOT_NULL")
	private String testId;
	
	private List<AnswerRequest> answerRequests;
}
