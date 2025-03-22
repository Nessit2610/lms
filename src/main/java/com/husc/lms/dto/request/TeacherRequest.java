package com.husc.lms.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherRequest {

	private String username;
	
    private String password;
    
    private String email;
    
    private String fullName;

    private String firstName;

    private String lastName;

}
