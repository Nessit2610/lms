package com.husc.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentChapterResponse;
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
	
//	@PostMapping
//	public ResponseEntity<Comment> addComment(@RequestBody Comment comment){
//		return ResponseEntity.ok(commentService.saveCommentWithReadStatusAndNotification(comment));
//	}	
	
//	@GetMapping("/chapter/{chapterId}")
//	public ResponseEntity<List<CommentChapterResponse>> getComments(@PathVariable("chapterId") String chapterId) {
//	    List<CommentChapterResponse> response = commentService.getCommentsByChapterId(chapterId);
//	    return ResponseEntity.ok(response);
//	}

	@GetMapping("/chapter/{chapterId}")
	public APIResponse<Page<CommentChapterResponse>> getComments(
	        @PathVariable("chapterId") String chapterId,
	        @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
	        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
	        @RequestParam(name = "replyPageNumber", defaultValue = "0") int replyPageNumber,
	        @RequestParam(name = "replyPageSize", defaultValue = "5") int replyPageSize) {
	    
	    // Gọi service để lấy comments và replies phân trang
	    Page<CommentChapterResponse> response = commentService.getCommentsByChapterId(
	            chapterId, pageNumber, pageSize, replyPageNumber, replyPageSize);

	    // Trả về kết quả dưới dạng APIResponse
	    return APIResponse.<Page<CommentChapterResponse>>builder()
	            .result(response)
	            .build();
	}


	
	@GetMapping("/chapter/unreadCommentsOfCourse/{courseId}")
	public APIResponse<CommentsOfChapterInLessonOfCourseResponse> getUnreadCommentsOfCourse(
	        @PathVariable("courseId") String courseId,
	        @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
	        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
	    
	    CommentsOfChapterInLessonOfCourseResponse response = commentService.getStructuredUnreadComments(courseId, pageNumber, pageSize);
	    return APIResponse.<CommentsOfChapterInLessonOfCourseResponse>builder()
	            .result(response)
	            .build();
	}

	
//	@PostMapping("/chapter/read")
//    public APIResponse<Boolean> setNotificationAsReadByAccount(@RequestBody List<Comment> comments) {
//		commentReadStatusService.setCommentsAsReadByAccount(comments);
//		return APIResponse.<Boolean>builder()
//				.result(true)
//				.build();
//    }
	@PostMapping("/chapter/read")
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
}
