package com.husc.lms.dto.update;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

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

	@NotNull(message = "NOT_NULL")
	private String testInGroupId;
	
	@NotNull(message = "NOT_NULL")
	private String title;

    private String description;
    
    @NotNull(message = "NOT_NULL")
    private OffsetDateTime startedAt;
    
    @NotNull(message = "NOT_NULL")
    private OffsetDateTime expiredAt;
    
    @NotNull(message = "NOT_NULL")
    private List<TestQuestionRequest> listQuestionRequest;
}
