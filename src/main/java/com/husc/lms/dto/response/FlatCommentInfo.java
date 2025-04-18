package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlatCommentInfo {
    private String courseId;
    private String courseTitle;

    private String lessonId;
    private Integer lessonOrder;

    private String chapterId;
    private Integer chapterOrder;

    private String chapterTitle;

    private String username;
    private String detail;
    private OffsetDateTime createdDate;
}
