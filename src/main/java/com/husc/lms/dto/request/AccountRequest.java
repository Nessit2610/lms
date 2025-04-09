package com.husc.lms.dto.request;


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
	private String password;
	
	private String email;

	
	
}
