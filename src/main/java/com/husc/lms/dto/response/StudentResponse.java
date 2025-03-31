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
public class StudentResponse {

	private String fullName;
    
	private String code;
	
	private Date dateOfBirth;
	
	private String gender;
	
	private String email;
	
	private String phoneNumber;
	
	private String contactAddress;
	
	private String avatar;
	
	private ClassOfStudentResponse classId;
	
	private MajorResponse majorId;

}
