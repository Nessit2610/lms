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
public class TeacherRequest {
	
	@NotNull(message = "NOT_NULL")
	private String email;
	@NotNull(message = "NOT_NULL")
    private String password;
	@NotNull(message = "NOT_NULL")
    private String fullName;

}
