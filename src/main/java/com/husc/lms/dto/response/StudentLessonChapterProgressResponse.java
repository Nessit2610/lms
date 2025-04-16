package com.husc.lms.dto.response;

import java.util.Date;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLessonChapterProgressResponse {

	private Boolean isCompleted;

    private Date completeAt;
}
