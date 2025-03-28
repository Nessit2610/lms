package com.husc.lms.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassOfStudentResponse {

	private String code;

    private String status;

    private String name;

    private TeacherResponse teacher;

    private CourseResponse course;
}
