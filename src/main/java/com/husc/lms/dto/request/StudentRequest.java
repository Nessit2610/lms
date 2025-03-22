package com.husc.lms.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

	private String username;
	
    private String password;
    
    private String email;
    
    private String fullName;

    private String firstName;

    private String lastName;

    private Date dateOfBirth;

    private String placeOfBirth;

    private String gender;
}
