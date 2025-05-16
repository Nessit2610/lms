package com.husc.lms.dto.update;

import java.time.OffsetDateTime;
import java.util.List;

import com.husc.lms.dto.request.TestQuestionRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestInGroupUpdateRequest {

	@NotNull
	private String testInGroupId;
	
	@NotNull
	private String title;

    private String description;
    
    private OffsetDateTime startedAt;
    
    @NotNull
    private OffsetDateTime expiredAt;
    
    @NotNull
    private List<TestQuestionRequest> listQuestionRequest;
}
