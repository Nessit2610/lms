package com.husc.lms.dto.request;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestInGroupRequest {

	@NotNull
	private String groupId;
	
	@NotNull
	private String title;

    private String description;
    
    @NotNull
    private Date expiredAt;
    
    @NotNull
    private List<TestQuestionRequest> listQuestionRequest;
}
