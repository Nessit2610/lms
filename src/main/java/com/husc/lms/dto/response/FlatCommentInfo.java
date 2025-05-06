package com.husc.lms.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlatCommentInfo {
//	private String commentId;
	
    private String courseId;
    private String courseTitle;

    private String lessonId;
    private Integer lessonOrder;

    private String chapterId;
    private Integer chapterOrder;

    private String chapterTitle;

    private String username;
    private String avatar;
    private String detail;
    private OffsetDateTime createdDate;
}
