package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestQuestionResponse {

	private int point;
	private String content;
	private String type;
	private String options;
	private String correctAnswers;
	
}
