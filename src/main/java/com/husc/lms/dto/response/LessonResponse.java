package com.husc.lms.dto.response;

import java.util.List;

import com.husc.lms.entity.LessonMaterial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {

	private String id;
	private String description;
    private Integer order;
    List<LessonMaterialResponse> lessonMaterial;
}
