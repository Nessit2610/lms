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
public class LessonRequest {
	
	@NotNull(message = "NOT_NULL")
	private String courseId;
	
    private String description;
    
    @NotNull(message = "NOT_NULL")
    private Integer order;
}
