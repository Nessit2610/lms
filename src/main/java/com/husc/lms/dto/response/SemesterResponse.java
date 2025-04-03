package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterResponse {

	private String name;

    private String status;
    
    private AcademicYearResponse academicYear;
}
