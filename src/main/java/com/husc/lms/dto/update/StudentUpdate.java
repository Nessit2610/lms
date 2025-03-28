package com.husc.lms.dto.update;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUpdate {
	
    private Date dateOfBirth;
    private String placeOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String contactAddress;
    private String permanentResidence;
    private byte[] avatar;
    
    
}
