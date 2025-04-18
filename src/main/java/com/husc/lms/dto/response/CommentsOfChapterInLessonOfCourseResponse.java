package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsOfChapterInLessonOfCourseResponse {
    private String courseId;
    private String courseTitle;
    private int totalCommentsOfCourse; // ✅ Tổng số comment của course
    private List<LessonWithChapters> lessons;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonWithChapters {
        private String lessonId;
        private String lessonTitle;
        private Integer lessonOrder;
        private List<ChapterWithComments> chapters;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterWithComments {
        private String chapterId;
        private String chapterTitle;
        private Integer chapterOrder;
        private int totalCommentsOfChapter; // ✅ Tổng số comment của chapter
        private List<CommentResponse> comments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private String username;
        private String detail;
        private OffsetDateTime createdDate;
    }
}

