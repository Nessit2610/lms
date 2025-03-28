package com.husc.lms.dto.response;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentOfClassResponse {
	
	private String code;
	private String fullName;
	private Date dateOfBirth;
}
