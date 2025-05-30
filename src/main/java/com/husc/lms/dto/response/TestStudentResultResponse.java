package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestStudentResultResponse {

	private String id;
	
	private StudentViewResponse student;
	
	private TestInGroupViewResponse testInGroup;

	private int totalCorrect; 

    private double score; 

    private OffsetDateTime startedAt;
    
    private OffsetDateTime submittedAt;
    
    private List<TestStudentAnswerResponse> testStudentAnswer;
}
