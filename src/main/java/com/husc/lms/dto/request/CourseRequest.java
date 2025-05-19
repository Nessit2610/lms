package com.husc.lms.dto.request;


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
public class CourseRequest {

	@NotNull(message = "NOT_NULL")
	private String name;
	
	private String description;
	
	@NotNull(message = "NOT_NULL")
	private String status; //private or public or request

	@NotNull(message = "NOT_NULL")
	private String learningDurationType; // Thoi han khoa hoc
	
	@NotNull(message = "NOT_NULL")
    private Date startDate;

    private Date endDate;
    
    @NotNull(message = "NOT_NULL")
    private String feeType;

    private Double price;
    
    @NotNull(message = "NOT_NULL")
    private String majorId;
}
