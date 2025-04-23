package com.husc.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsOfChapterInLessonOfCourseResponse {
    private String courseId;
    private String courseTitle;
    private int totalCommentsOfCourse;
    private int totalUnreadComment;
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
        private int totalCommentsOfChapter;
        private int totalUnreadComment;
        private List<CommentResponse> comments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private String username;
        private String avatar;
        private String detail;
        private OffsetDateTime createdDate;
        private List<CommentReplyResponse> commentReplies;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentReplyResponse {
        private String username;
        private String avatar;
        private String detail;
        private OffsetDateTime createdDate;
    }
}
