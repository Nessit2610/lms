package com.husc.lms.dto.response;

import com.husc.lms.entity.Room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSectionResponse {
	
	private String id;
	private String name;
	private SemesterResponse semester;
	private CurriculumSubjectResponse curriculumSubject;
	private TeacherResponse teacher;
	private RoomResponse room;
}
