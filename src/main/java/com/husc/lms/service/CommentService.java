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
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentReplyResponse;
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
import com.husc.lms.enums.NotificationType;
import com.husc.lms.enums.StatusCourse;
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
//		var context = SecurityContextHolder.getContext();

//        Account account = accountRepository.findByUsername(context.getAuthentication().getName())
//            .orElseThrow(() -> new RuntimeException("Account not found"));
		Account account = accountRepository.findByUsernameAndDeletedDateIsNull(message.getAccountId())
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

        // Lấy comments phân trang
        Page<Comment> pagedComments = commentRepository.findByChapter(chapter, pageable);

        // Map comments thành CommentChapterResponse, và phân trang replies
        return pagedComments.map(comment -> {
            Account commentAccount = comment.getAccount();
            String commentAvatar = "";

            if (commentAccount.getStudent() != null) {
                commentAvatar = commentAccount.getStudent().getAvatar();
            } else if (commentAccount.getTeacher() != null) {
                commentAvatar = commentAccount.getTeacher().getAvatar();
            }

            // Phân trang replies
            Page<CommentReply> pagedReplies = commentReplyRepository.findByComment(comment, replyPageable);

            List<CommentReplyResponse> replyResponses = pagedReplies.getContent().stream()
                    .map(reply -> {
                        Account replyAccount = reply.getReplyAccount();
                        String replyAvatar = "";

                        if (replyAccount.getStudent() != null) {
                            replyAvatar = replyAccount.getStudent().getAvatar();
                        } else if (replyAccount.getTeacher() != null) {
                            replyAvatar = replyAccount.getTeacher().getAvatar();
                        }

                        return CommentReplyResponse.builder()
                                .commentReplyId(reply.getId())
                                .username(replyAccount.getUsername())
                                .avatar(replyAvatar)
                                .detail(reply.getDetail())
                                .createdDate(reply.getCreatedDate())
                                .build();
                    })
                    .collect(Collectors.toList());

            return new CommentChapterResponse(
                    comment.getId(),
                    commentAccount.getUsername(),
                    commentAvatar,
                    comment.getDetail(),
                    comment.getCreatedDate(),
                    replyResponses
            );
        });
    }



    
    
    public List<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId) {
    	return commentRepository.findUnreadCommentsByCourseId(courseId);
    }
    public Page<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId, int pageNumber, int pageSize) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        
        return commentRepository.findUnreadCommentsByCourseId(courseId, pageRequest);
    }

//    public CommentsOfChapterInLessonOfCourseResponse getStructuredUnreadComments(String courseId) {
//        List<FlatCommentInfo> flatList = commentRepository.findStructuredUnreadComments(courseId);
//
//        if (flatList.isEmpty()) return null;
//
//        CommentsOfChapterInLessonOfCourseResponse response = new CommentsOfChapterInLessonOfCourseResponse();
//        response.setCourseId(flatList.get(0).getCourseId());
//        response.setCourseTitle(flatList.get(0).getCourseTitle());
//
//        Map<String, CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> lessonMap = new LinkedHashMap<>();
//        int totalCommentsOfCourse = 0;
//
//        for (FlatCommentInfo flat : flatList) {
//            totalCommentsOfCourse++;
//
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
//            // ✅ Thêm avatar vào comment
//            chapter.getComments().add(new CommentsOfChapterInLessonOfCourseResponse.CommentResponse(
//            	    flat.getUsername(),
//            	    flat.getAvatar() != null ? flat.getAvatar() : "",  // Nếu avatar không có thì để rỗng
//            	    flat.getDetail(),
//            	    flat.getCreatedDate(),
//            	    new ArrayList<>()  // Danh sách commentReplies rỗng nếu không có reply
//            	));
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
//        response.setTotalCommentsOfCourse(totalCommentsOfCourse);
//
//        return response;
//    }
    public CommentsOfChapterInLessonOfCourseResponse getStructuredUnreadComments(String courseId, int pageNumber, int pageSize) {
        List<FlatCommentInfo> flatList = commentRepository.findStructuredUnreadComments(courseId);

        if (flatList.isEmpty()) return null;

        CommentsOfChapterInLessonOfCourseResponse response = new CommentsOfChapterInLessonOfCourseResponse();
        response.setCourseId(flatList.get(0).getCourseId());
        response.setCourseTitle(flatList.get(0).getCourseTitle());

        int totalCommentsOfCourse = flatList.size();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalCommentsOfCourse);
        List<FlatCommentInfo> paginatedFlatList = flatList.subList(fromIndex, toIndex);

        Map<String, CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> lessonMap = new LinkedHashMap<>();

        for (FlatCommentInfo flat : paginatedFlatList) {
            lessonMap.putIfAbsent(flat.getLessonId(),
                new CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters(
                    flat.getLessonId(),
                    flat.getLessonId(),
                    flat.getLessonOrder(),
                    new ArrayList<>()
                )
            );

            var lesson = lessonMap.get(flat.getLessonId());
            var chapterList = lesson.getChapters();

            var chapter = chapterList.stream()
                .filter(ch -> ch.getChapterId().equals(flat.getChapterId()))
                .findFirst()
                .orElseGet(() -> {
                    var ch = new CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments(
                        flat.getChapterId(),
                        flat.getChapterTitle(),
                        flat.getChapterOrder(),
                        0,
                        0,
                        new ArrayList<>()
                    );
                    chapterList.add(ch);
                    return ch;
                });

            chapter.getComments().add(new CommentsOfChapterInLessonOfCourseResponse.CommentResponse(
                flat.getUsername(),
                flat.getAvatar() != null ? flat.getAvatar() : "",
                flat.getDetail(),
                flat.getCreatedDate(),
                new ArrayList<>()
            ));

            chapter.setTotalCommentsOfChapter(chapter.getTotalCommentsOfChapter() + 1);
        }

        List<CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> sortedLessons = new ArrayList<>(lessonMap.values());
        sortedLessons.sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters::getLessonOrder));
        for (var lesson : sortedLessons) {
            lesson.getChapters().sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments::getChapterOrder));
        }

        response.setLessons(sortedLessons);
        response.setTotalCommentsOfCourse(totalCommentsOfCourse); // ✅ Tổng số comment vẫn là của toàn bộ

        return response;
    }


    @Transactional
    public void saveCommentWithReadStatusAndNotification(CommentMessage commentMessage) {
        // Lấy thông tin cần thiết từ message
        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(commentMessage.getAccountId())
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

            // Nếu là người đăng comment
            if (studentAccount.getId().equals(account.getId())) {
                // Thêm trạng thái đã đọc
                readStatuses.add(CommentReadStatus.builder()
                    .account(studentAccount)
                    .comment(savedComment)
                    .isRead(true) // đã đọc rồi vì chính họ đăng
                    .build()
                );
                continue; // Không tạo thông báo
            }

            // Những người còn lại - thêm trạng thái chưa đọc và tạo thông báo
            readStatuses.add(CommentReadStatus.builder()
                .account(studentAccount)
                .comment(savedComment)
                .isRead(false)
                .build()
            );

            notifications.add(Notification.builder()
                .account(studentAccount)
                .comment(savedComment)
                .type(NotificationType.COMMENT)
                .description(comment.getDetail())
                .isRead(false)
                .createdAt(new Date())
                .build()
            );
        }


        // Lưu vào DB
        commentReadStatusRepository.saveAll(readStatuses);
        notificationRepository.saveAll(notifications);

        // Gửi thông báo real-time
        for (Notification	 notification : notifications) {
            String destination = "/topic/notifications/" + notification.getAccount().getUsername();
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", notification.getDescription());
            payload.put("chapterId", notification.getComment().getChapter().getId());
            payload.put("createdDate", notification.getCreatedAt());
            messagingTemplate.convertAndSend(destination, payload);
        }
    }


    public List<Student> findEligibleStudents(String lessonId, String chapterId) {
        List<String> studentIdsByLesson = studentLessonProgressRepository.findStudentIdsByLessonCompleted(lessonId);
        List<String> studentIdsByChapter = studentLessonChapterProgressRepository.findStudentIdsByChapterCompleted(chapterId);

        Set<String> uniqueStudentIds = new HashSet<>();
        uniqueStudentIds.addAll(studentIdsByLesson);
        uniqueStudentIds.addAll(studentIdsByChapter);

        return studentRepository.findAllById(uniqueStudentIds);
    }

}
