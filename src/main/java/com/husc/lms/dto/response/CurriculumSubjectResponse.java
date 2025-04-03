package com.husc.lms.dto.response;

import com.husc.lms.entity.Subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumSubjectResponse {

	private SubjectResponse subject;
	private Integer credit;
	private Boolean required;
}
