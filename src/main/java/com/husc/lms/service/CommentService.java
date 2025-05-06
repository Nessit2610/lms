package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.request.CommentUpdateMessage;
import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentMessageResponse;
import com.husc.lms.dto.response.CommentOfCourseResponse;
import com.husc.lms.dto.response.CommentReplyResponse;
import com.husc.lms.dto.response.CommentUpdateMessageResponse;
import com.husc.lms.dto.response.CommentsOfChapterInLessonOfCourseResponse;
import com.husc.lms.dto.response.FlatCommentInfo;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReadStatus;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.CommentType;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.enums.StatusCourse;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentReadStatusRepository;
import com.husc.lms.repository.CommentReplyRepository;
import com.husc.lms.repository.CommentRepository;
//import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final CommentReplyRepository commentReplyRepository;
	private final ChapterRepository chapterRepository;
	private final CourseRepository courseRepository;
	private final AccountRepository accountRepository;
	private final StudentRepository studentRepository;
	private final CommentReadStatusRepository commentReadStatusRepository;
	private final StudentLessonChapterProgressRepository studentLessonChapterProgressRepository;
	private final StudentLessonProgressRepository studentLessonProgressRepository;
	private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public void handleWebSocketComment(CommentMessage message) {

		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(message.getUsername())
				.orElseThrow(() -> new RuntimeException("Account not found"));
        Chapter chapter = chapterRepository.findById(message.getChapterId())
            .orElseThrow(() -> new RuntimeException("Chapter not found"));
        Course course = courseRepository.findById(message.getCourseId())
            .orElseThrow(() -> new RuntimeException("Course not found"));

        Comment comment = Comment.builder()
            .account(account)
            .chapter(chapter)
            .course(course)
            .detail(message.getDetail())
            .createdDate(OffsetDateTime.now())
            .build();

        commentRepository.save(comment);
    }
        
    public Page<CommentChapterResponse> getCommentsByChapterId(String chapterId, int pageNumber, int pageSize, int replyPageNumber, int replyPageSize) {

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Pageable replyPageable = PageRequest.of(replyPageNumber, replyPageSize);

//        Page<Comment> pagedComments = commentRepository.findByChapterOrderByCreatedDateDesc(chapter, pageable);
        Page<Comment> pagedComments = commentRepository.findByChapterAndDeletedDateIsNullOrderByCreatedDateDesc(chapter, pageable);
        return pagedComments.map(comment -> {
            Account commentAccount = comment.getAccount();
            String usernameOwner = commentAccount.getUsername();
            String commentAvatar = "";
            String fullnameOwner = "";

            if (commentAccount.getStudent() != null) {
                commentAvatar = Optional.ofNullable(commentAccount.getStudent().getAvatar()).orElse("");
                fullnameOwner = commentAccount.getStudent().getFullName();
            } else if (commentAccount.getTeacher() != null) {
                commentAvatar = Optional.ofNullable(commentAccount.getTeacher().getAvatar()).orElse("");
                fullnameOwner = commentAccount.getTeacher().getFullName();
            }

            Page<CommentReply> pagedReplies = commentReplyRepository.findByComment(comment, replyPageable);

            List<CommentReplyResponse> replyResponses = new ArrayList<>();

            for (CommentReply reply : pagedReplies.getContent()) {
                Account replyAccount = reply.getReplyAccount();
                String replyAvatar = "";
                String replyFullname = "";

                if (replyAccount.getStudent() != null) {
                    replyAvatar = Optional.ofNullable(replyAccount.getStudent().getAvatar()).orElse("");
                    replyFullname = replyAccount.getStudent().getFullName();
                } else if (replyAccount.getTeacher() != null) {
                    replyAvatar = Optional.ofNullable(replyAccount.getTeacher().getAvatar()).orElse("");
                    replyFullname = replyAccount.getTeacher().getFullName();
                }

                CommentReplyResponse replyResponse = CommentReplyResponse.builder()
                		.commentId(reply.getComment().getId())
                        .commentReplyId(reply.getId())
                        .usernameOwner(usernameOwner)
                        .fullnameOwner(fullnameOwner)
                        .usernameReply(replyAccount.getUsername())
                        .avatarReply(replyAvatar)
                        .fullnameReply(replyFullname)
                        .detail(reply.getDetail())
                        .createdDate(reply.getCreatedDate())
                        .build();

                replyResponses.add(replyResponse);
            }

            return CommentChapterResponse.builder()
                    .commentId(comment.getId())
                    .username(usernameOwner)
                    .fullname(fullnameOwner)
                    .avatar(commentAvatar)
                    .detail(comment.getDetail())
                    .createdDate(comment.getCreatedDate())
                    .commentReplyResponses(replyResponses)
                    .build();
        });
    }
    
    public List<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId) {
    	return commentRepository.findUnreadCommentsByCourseId(courseId);
    }
    
    public Page<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId, int pageNumber, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        
        return commentRepository.findUnreadCommentsByCourseId(courseId, pageRequest);
    }

//    public CommentsOfChapterInLessonOfCourseResponse getStructuredUnreadComments(String courseId, int pageNumber, int pageSize) {
//        List<FlatCommentInfo> flatList = commentRepository.findStructuredUnreadComments(courseId);
//
//        if (flatList.isEmpty()) return null;
//
//        CommentsOfChapterInLessonOfCourseResponse response = new CommentsOfChapterInLessonOfCourseResponse();
//        response.setCourseId(flatList.get(0).getCourseId());
//        response.setCourseTitle(flatList.get(0).getCourseTitle());
//
//        int totalCommentsOfCourse = flatList.size();
//        int fromIndex = pageNumber * pageSize;
//        int toIndex = Math.min(fromIndex + pageSize, totalCommentsOfCourse);
//        List<FlatCommentInfo> paginatedFlatList = flatList.subList(fromIndex, toIndex);
//
//        Map<String, CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> lessonMap = new LinkedHashMap<>();
//
//        for (FlatCommentInfo flat : paginatedFlatList) {
//            lessonMap.putIfAbsent(flat.getLessonId(),
//                new CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters(
//                    flat.getLessonId(),
//                    flat.getLessonId(),
//                    flat.getLessonOrder(),
//                    new ArrayList<>()
//                )
//            );
//
//            var lesson = lessonMap.get(flat.getLessonId());
//            var chapterList = lesson.getChapters();
//
//            var chapter = chapterList.stream()
//                .filter(ch -> ch.getChapterId().equals(flat.getChapterId()))
//                .findFirst()
//                .orElseGet(() -> {
//                    var ch = new CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments(
//                        flat.getChapterId(),
//                        flat.getChapterTitle(),
//                        flat.getChapterOrder(),
//                        0,
//                        0,
//                        new ArrayList<>()
//                    );
//                    chapterList.add(ch);
//                    return ch;
//                });
//
//            chapter.getComments().add(new CommentsOfChapterInLessonOfCourseResponse.CommentResponse(
//                flat.getUsername(),
//                flat.getAvatar() != null ? flat.getAvatar() : "",
//                flat.getDetail(),
//                flat.getCreatedDate(),
//                new ArrayList<>()
//            ));
//
//            chapter.setTotalCommentsOfChapter(chapter.getTotalCommentsOfChapter() + 1);
//        }
//
//        List<CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> sortedLessons = new ArrayList<>(lessonMap.values());
//        sortedLessons.sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters::getLessonOrder));
//        for (var lesson : sortedLessons) {
//            lesson.getChapters().sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments::getChapterOrder));
//        }
//
//        response.setLessons(sortedLessons);
//        response.setTotalCommentsOfCourse(totalCommentsOfCourse); // ✅ Tổng số comment vẫn là của toàn bộ
//
//        return response;
//    }
    public Page<CommentOfCourseResponse> getTotalUnreadCommentsOfCourseForTeacher(int pageNumber, int pageSize) {
    	var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
    	
		Account teacherAccount = accountRepository.findByUsernameAndDeletedDateIsNull(name)
				.orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));
		
		if (teacherAccount.getTeacher() == null) {
	        return Page.empty();
	    }

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		
		Page<Course> coursesOfTeacher = courseRepository.findByTeacherAndDeletedDateIsNull(teacherAccount.getTeacher(), pageable);
		
		Page<CommentOfCourseResponse> result = coursesOfTeacher.map(courseOfTeacher -> {

		    int totalCommentsOfCourse = commentReadStatusRepository.countAllValidCommentReadStatusesByCourse(courseOfTeacher)
		    							+ commentReadStatusRepository.countAllValidCommentReplyReadStatusesByCourse(courseOfTeacher);
		    int totalUnreadCommentOfCourse = commentReadStatusRepository.countUnreadValidCommentReadStatusesByCourse(courseOfTeacher)
		    							+ commentReadStatusRepository.countUnreadValidCommentReplyReadStatusesByCourse(courseOfTeacher);

		    List<CommentOfCourseResponse.CommentOfLesson> commentOfLesson = courseOfTeacher.getLesson().stream().map(lessonOfCourse -> {
		        int totalCommentOfLesson = commentReadStatusRepository.countAllValidCommentReadStatusesByLessonInCourse(lessonOfCourse)
		        						+ commentReadStatusRepository.countAllValidCommentReplyReadStatusesByLessonInCourse(lessonOfCourse);
		        int totalUnreadCommentsOfLesson = commentReadStatusRepository.countUnreadCommentReadStatusesByLessonInCourse(lessonOfCourse)
		        						+ commentReadStatusRepository.countUnreadCommentReplyReadStatusesByLessonInCourse(lessonOfCourse);

		        List<CommentOfCourseResponse.CommentOfChapter> commentOfChapter = lessonOfCourse.getChapter().stream().map(chapterOfLesson -> {
		            int totalCommentOfChapter = commentReadStatusRepository.countAllValidCommentReadStatusesByChapter(chapterOfLesson)
		            					+ commentReadStatusRepository.countAllValidCommentReplyReadStatusesByChapter(chapterOfLesson);
		            int totalUnreadCommentsOfChapter = commentReadStatusRepository.countUnreadCommentReadStatusesByChapter(chapterOfLesson)
		            					+ commentReadStatusRepository.countUnreadCommentReplyReadStatusesByChapter(chapterOfLesson);

		            return CommentOfCourseResponse.CommentOfChapter.builder()
		                    .chapterId(chapterOfLesson.getId())
		                    .chapterName(chapterOfLesson.getName())
		                    .chapterOrder(chapterOfLesson.getOrder())
		                    .totalCommentsOfChapter(totalCommentOfChapter)
		                    .totalUnreadCommentsOfChapter(totalUnreadCommentsOfChapter)
		                    .build();
		        }).toList();

		        return CommentOfCourseResponse.CommentOfLesson.builder()
		                .lessonId(lessonOfCourse.getId())
		                .lessonName(lessonOfCourse.getDescription())
		                .lessonOrder(lessonOfCourse.getOrder())
		                .totalCommentsOfLesson(totalCommentOfLesson)
		                .totalUnreadCommentsOfLesson(totalUnreadCommentsOfLesson)
		                .commentsOfChapter(commentOfChapter)
		                .build();
		    }).toList();

		    return CommentOfCourseResponse.builder()
		            .courseId(courseOfTeacher.getId())
		            .courseName(courseOfTeacher.getName())
		            .totalCommentsOfCourse(totalCommentsOfCourse)
		            .totalUnreadCommentsfCourse(totalUnreadCommentOfCourse)
		            .commentsOfLesson(commentOfLesson)
		            .build();

		}); // <-- Đây là dấu đóng đúng của map()

		return result;
		
//		for (Course courseOfTeacher : coursesOfTeacher) {
//			List<Comment> commentsOfCourse = commentRepository.findByCourse(courseOfTeacher);
//			int totalCommentsOfCourse = commentReadStatusRepository.countAllByCommentsAndAccount(commentsOfCourse, teacherAccount);
//			int totalUnreadCommentOfCourse = commentReadStatusRepository.countUnreadByCommentsAndAccount(commentsOfCourse, teacherAccount);
//			
//			
//		}
//    	return null;
    }

    @Transactional
    public CommentMessageResponse saveCommentWithReadStatusAndNotification(CommentMessage commentMessage) {
        // Lấy thông tin cần thiết từ message
        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(commentMessage.getUsername())
            .orElseThrow(() -> new RuntimeException("Account not found"));

        Chapter chapter = chapterRepository.findById(commentMessage.getChapterId())
            .orElseThrow(() -> new RuntimeException("Chapter not found"));

        Course course = courseRepository.findById(commentMessage.getCourseId())
            .orElseThrow(() -> new RuntimeException("Course not found"));

        // Tạo và lưu comment
        Comment comment = Comment.builder()
            .account(account)
            .chapter(chapter)
            .course(course)
            .detail(commentMessage.getDetail())
            .createdDate(OffsetDateTime.now())
            .build();

        Comment savedComment = commentRepository.save(comment);

        // Lấy lesson từ chapter
        Lesson lesson = chapter.getLesson();
        if (lesson == null) {
            throw new IllegalArgumentException("Chapter không thuộc lesson nào.");
        }

        // Tìm các student đủ điều kiện
        List<Student> eligibleStudents = findEligibleStudents(lesson.getId(), chapter.getId());
        List<Notification> notifications = new ArrayList<>();
        List<CommentReadStatus> readStatuses = new ArrayList<>();

        for (Student student : eligibleStudents) {
            Account studentAccount = student.getAccount();

            if (studentAccount.getId().equals(account.getId())) {
                readStatuses.add(CommentReadStatus.builder()
                    .account(studentAccount)
                    .comment(savedComment)
                    .isRead(true)
                    .build());
                continue;
            }

            readStatuses.add(CommentReadStatus.builder()
                .account(studentAccount)
                .comment(savedComment)
                .commentType(CommentType.COMMENT)
                .isRead(false)
                .build());

            notifications.add(Notification.builder()
                .account(studentAccount)
                .comment(savedComment)
                .type(NotificationType.COMMENT)
                .description(comment.getDetail())
                .isRead(false)
                .createdAt(new Date())
                .build());
        }

        // Lưu vào DB
        commentReadStatusRepository.saveAll(readStatuses);
        notificationRepository.saveAll(notifications);

        // Gửi thông báo real-time
        for (Notification notification : notifications) {
            String destination = "/topic/notifications/" + notification.getAccount().getUsername();
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", notification.getDescription());
            payload.put("chapterId", notification.getComment().getChapter().getId());
            payload.put("createdDate", notification.getCreatedAt());
            messagingTemplate.convertAndSend(destination, payload);
        }

        // ✅ Trả về CommentMessageResponse
        return CommentMessageResponse.builder()
        	    .chapterId(savedComment.getChapter().getId())
        	    .courseId(savedComment.getCourse().getId())
        	    .commentId(savedComment.getId())
        	    .username(account.getUsername())
        	    .fullname(
        	        account.getStudent() != null && account.getStudent().getFullName() != null
        	            ? account.getStudent().getFullName()
        	            : (account.getTeacher() != null && account.getTeacher().getFullName() != null
        	                ? account.getTeacher().getFullName()
        	                : "")
        	    )
        	    .avatar(
        	        account.getStudent() != null && account.getStudent().getAvatar() != null
        	            ? account.getStudent().getAvatar()
        	            : (account.getTeacher() != null && account.getTeacher().getAvatar() != null
        	                ? account.getTeacher().getAvatar()
        	                : "")
        	    )
        	    .detail(comment.getDetail())
        	    .createdDate(comment.getCreatedDate())
        	    .commentReplyResponses(List.of())
        	    .build();

    }


    public List<Student> findEligibleStudents(String lessonId, String chapterId) {
        List<String> studentIdsByLesson = studentLessonProgressRepository.findStudentIdsByLessonCompleted(lessonId);
        List<String> studentIdsByChapter = studentLessonChapterProgressRepository.findStudentIdsByChapterCompleted(chapterId);

        Set<String> uniqueStudentIds = new HashSet<>();
        uniqueStudentIds.addAll(studentIdsByLesson);
        uniqueStudentIds.addAll(studentIdsByChapter);

        return studentRepository.findAllById(uniqueStudentIds);
    }

	public CommentUpdateMessageResponse updateComment(CommentUpdateMessage message) {
		Comment changeComment = commentRepository.findById(message.getCommentId())
		        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

	    Account ownerAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getUsernameOwner())
	        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));

	    if (!changeComment.getAccount().equals(ownerAccount)) {
	        throw new AppException(ErrorCode.OWNER_NOT_MATCH); // hoặc tạo error code phù hợp
	    }

	    changeComment.setDetail(message.getNewDetail());
	    changeComment.setUpdateDateAt(OffsetDateTime.now());
	    commentRepository.save(changeComment);

	    return CommentUpdateMessageResponse.builder()
	        .commentId(changeComment.getId())
	        .courseId(changeComment.getCourse().getId())
	        .chapterId(changeComment.getChapter().getId())
	        .newDetail(changeComment.getDetail())
	        .updateDate(changeComment.getUpdateDateAt())
	        .usernameOwner(changeComment.getAccount().getUsername())
	        .avatarOwner(changeComment.getAccount().getStudent() != null
	                    ? changeComment.getAccount().getStudent().getAvatar()
	                    : changeComment.getAccount().getTeacher() != null
	                        ? changeComment.getAccount().getTeacher().getAvatar()
	                        : null)
	        .fullnameOwner(changeComment.getAccount().getStudent() != null
	                    ? changeComment.getAccount().getStudent().getFullName()
	                    : changeComment.getAccount().getTeacher() != null
	                        ? changeComment.getAccount().getTeacher().getFullName()
	                        : null)
	        .build();
	}

	public Boolean deleteComment(CommentUpdateMessage message) {
	    Comment deleteComment = commentRepository.findById(message.getCommentId())
	        .filter(comment -> comment.getDeletedDate() == null)
	        .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

	    Account ownerAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getUsernameOwner())
	        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOTFOUND));

	    if (!deleteComment.getAccount().getId().equals(ownerAccount.getId())) {
	        throw new AppException(ErrorCode.OWNER_NOT_MATCH);
	    }

	    OffsetDateTime now = OffsetDateTime.now();
	    deleteComment.setDeletedBy(message.getUsernameOwner());
	    deleteComment.setDeletedDate(now);

	    // Xử lý soft delete tất cả các comment replies nếu có
	    if (deleteComment.getCommentReplies() != null) {
	        deleteComment.getCommentReplies().forEach(reply -> {
	            if (reply.getDeletedDate() == null) {
	                reply.setDeletedBy(message.getUsernameOwner());
	                reply.setDeletedDate(now);
	            }
	        });
	    }

	    commentRepository.save(deleteComment); // save sẽ cascade nếu được cấu hình
	    return true;
	}


}
