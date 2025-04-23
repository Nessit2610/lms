package com.husc.lms.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

	
	@Pattern(
		    regexp = "^[a-zA-Z0-9._%+-]+@husc\\.edu\\.vn$",
		    message = "EMAIL_INVALID"
	)
	private String email;
	
    private String password;
    
    private String fullName;

    private String majorID;
}
