package com.husc.lms.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonQuizRequest {

	private String idLesson;
	
	private String question;
	    
    private String option;

    private String answer;
}
