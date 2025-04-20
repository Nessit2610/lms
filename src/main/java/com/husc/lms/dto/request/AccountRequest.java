package com.husc.lms.dto.request;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
	
	@Size(min = 8, message = "PASSWORD_INVALID")
//	@Pattern(
//	    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//	    message = "PASSWORD_INVALID"
//	)
	private String password;
	
//	@Pattern(
//	    regexp = "^[a-zA-Z0-9._%+-]+@husc\\.edu\\.vn$",
//	    message = "EMAIL_INVALID"
//	)
	private String email;

	
	
}
