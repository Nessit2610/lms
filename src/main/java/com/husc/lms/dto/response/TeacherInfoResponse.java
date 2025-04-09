package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherInfoResponse {
	
	private String fullName;
    private String email;
    private String avatar;
    private String description;
    private String contact;
}
