package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestStudentAnswerResponse {

	private String id;
	
	private TestQuestionResponse testQuestion;
	
	private String answer;
	
	private boolean isCorrect;
}
