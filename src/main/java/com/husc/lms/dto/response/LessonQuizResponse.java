package com.husc.lms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonQuizResponse {

	private String id;
	
	private String question;
    
    private String option;

    private String answer;
    
    private String note;
}
