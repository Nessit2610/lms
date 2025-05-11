package com.husc.lms.dto.response;

import java.util.Date;
import java.util.List;

import com.husc.lms.entity.Teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

	private String id;
	
	private String title;
	
	private String text;
	
	private Date createdAt;
	
	private TeacherResponse teacher;
	
	private List<PostFileResponse> files;
}
