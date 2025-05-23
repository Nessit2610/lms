package com.husc.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentInChapterOfAccountResponse;
import com.husc.lms.dto.response.CommentOfCourseResponse;
import com.husc.lms.dto.response.CommentPostResponse;
import com.husc.lms.dto.response.CommentReplyResponse;
import com.husc.lms.dto.response.CommentsOfChapterInLessonOfCourseResponse;
import com.husc.lms.dto.response.CourseViewResponse;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReadStatus;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Notification;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.service.CommentReadStatusService;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService commentService;
	private final ChapterRepository chapterRepository;
	private final CommentReadStatusService commentReadStatusService;

	@GetMapping("/getCommentByChapter/details")
	public APIResponse<Page<CommentChapterResponse>> getComments(
			@RequestHeader("chapterId") String chapterId,
			@RequestHeader(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestHeader(name = "pageSize", defaultValue = "10") int pageSize) {

		// Gọi service để lấy comments và replies phân trang
		Page<CommentChapterResponse> response = commentService.getCommentsByChapterId(
				chapterId, pageNumber, pageSize);

		// Trả về kết quả dưới dạng APIResponse
		return APIResponse.<Page<CommentChapterResponse>>builder()
				.result(response)
				.build();
	}

	@GetMapping("/getCommentReplyByComment/details")
	public APIResponse<Page<CommentReplyResponse>> getCommentsReplyOfComment(
			@RequestHeader("commentId") String commentId,
			@RequestHeader(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestHeader(name = "pageSize", defaultValue = "10") int pageSize) {

		// Gọi service để lấy comments và replies phân trang
		Page<CommentReplyResponse> response = commentService.getCommentRepliesByCommentId(
				commentId, pageNumber, pageSize);

		// Trả về kết quả dưới dạng APIResponse
		return APIResponse.<Page<CommentReplyResponse>>builder()
				.result(response)
				.build();
	}

	@GetMapping("/unreadCommentsOfCourse")
	public APIResponse<Page<CommentOfCourseResponse>> getUnreadCommentsOfCourse(
			@RequestHeader(defaultValue = "0") int pageNumber,
			@RequestHeader(defaultValue = "20") int pageSize) {

		var response = commentService.getCoursesWithUnreadComments(pageNumber, pageSize);
		return APIResponse.<Page<CommentOfCourseResponse>>builder()
				.result(response)
				.build();
	}

	@GetMapping("/unreadCommentsOfLesson")
	public APIResponse<List<CommentOfCourseResponse.CommentOfLesson>> getUnreadCommentsOfLessons(
			@RequestHeader String courseId) {

		var response = commentService.getLessonsWithUnreadComments(courseId);
		return APIResponse.<List<CommentOfCourseResponse.CommentOfLesson>>builder()
				.result(response)
				.build();
	}

	@GetMapping("/unreadCommentsOfChapter")
	public APIResponse<List<CommentOfCourseResponse.CommentOfChapter>> getUnreadCommentsOfChapters(
			@RequestHeader String lessonId) {

		var response = commentService.getChaptersWithUnreadComments(lessonId);
		return APIResponse.<List<CommentOfCourseResponse.CommentOfChapter>>builder()
				.result(response)
				.build();
	}

	@PostMapping("/read")
	public APIResponse<String> setNotificationAsReadByAccount(@RequestBody List<Comment> comments) {
		try {
			commentReadStatusService.setCommentsAsReadByAccount(comments);
			return APIResponse.<String>builder()
					.result("Đã đánh dấu đã đọc")
					.build();
		} catch (Exception e) {
			return APIResponse.<String>builder()
					.result("Có lỗi xảy ra")
					.build();
		}
	}

	@GetMapping("/getCommentByPost/details")
	public APIResponse<Page<CommentPostResponse>> getCommentsByPost(
			@RequestHeader("postId") String postId,
			@RequestHeader(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestHeader(name = "pageSize", defaultValue = "10") int pageSize) {

		// Gọi service để lấy comments và replies phân trang
		Page<CommentPostResponse> response = commentService.getCommentsByPost(
				postId, pageSize, pageNumber);

		// Trả về kết quả dưới dạng APIResponse
		return APIResponse.<Page<CommentPostResponse>>builder()
				.result(response)
				.build();
	}

	@GetMapping("/myComments")
	public APIResponse<Page<CommentChapterResponse>> getMyCommentsByChapterId(
			@RequestParam String chapterId,
			@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

		Page<CommentChapterResponse> response = commentService.getCommentInChapterOfAccount(
				chapterId, pageNumber, pageSize);

		return APIResponse.<Page<CommentChapterResponse>>builder()
				.result(response)
				.build();
	}
}
