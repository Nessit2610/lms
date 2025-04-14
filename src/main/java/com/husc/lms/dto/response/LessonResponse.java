package com.husc.lms.dto.response;

import java.util.List;

import com.husc.lms.entity.LessonMaterial;
import com.husc.lms.entity.LessonQuiz;

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
    private List<LessonMaterialResponse> lessonMaterial;
    private List<LessonQuizResponse> lessonQuiz;
    private List<ChapterResponse> chapter;
    
}
