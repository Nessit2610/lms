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
public class UserCreationRequest {

	@Size(min = 5, message = "USERNAME_INVALID")
	private String username;
	
	@Size(min = 8, message = "PASSWORD_INVALID")
	private String password;
	
	private String email;

	
	
}
