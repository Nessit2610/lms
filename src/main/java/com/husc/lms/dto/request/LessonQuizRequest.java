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
public class LessonQuizRequest {
	
	@NotNull(message = "NOT_NULL")
	private String question;
	
	@NotNull(message = "NOT_NULL")
    private String option;

	@NotNull(message = "NOT_NULL")
    private String answer;
	
	private String note;
}
