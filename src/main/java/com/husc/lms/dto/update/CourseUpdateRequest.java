package com.husc.lms.dto.update;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {

	@NotNull(message = "NOT_NULL")
	private String idCourse;
	
	@NotNull(message = "NOT_NULL")
	private String name;

    private String description;
    
    @NotNull(message = "NOT_NULL")
    private Date startDate;

    @NotNull(message = "NOT_NULL")
    private String learningDurationType;
    
    private Date endDate;
    

    @NotNull(message = "NOT_NULL")
    private String majorId;

    @NotNull(message = "NOT_NULL")
    private String status;
    
    @NotNull(message = "NOT_NULL")
    private String feeType;

    private Double price;
    
    
}
