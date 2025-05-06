package com.husc.lms.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentOfCourseResponse {
	private String courseId;
	private String courseName;
	private Integer totalCommentsOfCourse;
	private Integer totalUnreadCommentsfCourse;
//	private List<CommentOfLesson> commentsOfLesson;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommentOfLesson {
		private String lessonId;
		private String lessonName;
		private Integer lessonOrder;
		private Integer totalCommentsOfLesson;
		private Integer totalUnreadCommentsOfLesson;
//		private List<CommentOfChapter> commentsOfChapter;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CommentOfChapter {
		private String chapterId;
		private String chapterName;
		private Integer chapterOrder;
		private Integer totalCommentsOfChapter;
		private Integer totalUnreadCommentsOfChapter;
	}
}
