package com.husc.lms.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResultViewResponse {

	private String id;
	
	private StudentViewResponse student;
	
	private TestInGroupViewResponse testInGroup;
	
	private int totalCorrect; 

    private double score; 
    
    private String status;

    private OffsetDateTime startedAt;

    private OffsetDateTime submittedAt;
}
