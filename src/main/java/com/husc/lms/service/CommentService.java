package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentsOfChapterInLessonOfCourseResponse;
import com.husc.lms.dto.response.FlatCommentInfo;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentNotification;
import com.husc.lms.entity.CommentReadStatus;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Student;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentNotificationRepository;
import com.husc.lms.repository.CommentReadStatusRepository;
import com.husc.lms.repository.CommentRepository;
//import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final ChapterRepository chapterRepository;
	private final CourseRepository courseRepository;
	private final AccountRepository accountRepository;
	private final StudentRepository studentRepository;
	private final CommentReadStatusRepository commentReadStatusRepository;
	private final StudentLessonChapterProgressRepository studentLessonChapterProgressRepository;
	private final StudentLessonProgressRepository studentLessonProgressRepository;
	private final CommentNotificationRepository commentNotificationRepository;
	
//    public Comment saveComment(Comment comment) {
//        comment.setCreatedDate(OffsetDateTime.now());
//        
//        return commentRepository.save(comment);
//    }

    @Transactional
    public void handleWebSocketComment(CommentMessage message) {
		var context = SecurityContextHolder.getContext();

        Account account = accountRepository.findByUsername(context.getAuthentication().getName())
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

    
    public List<Comment> getCommentsByChapter(Chapter chapter) {
        return commentRepository.findByChapterOrderByCreatedDateDesc(chapter);
    }
    
    public List<CommentChapterResponse> getCommentsByChapterId(String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        List<Comment> comments = getCommentsByChapter(chapter);

        return comments.stream()
                .map(comment -> new CommentChapterResponse(
                        comment.getAccount().getUsername(),
                        comment.getDetail(),
                        comment.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }
    
    public List<CommentChapterResponse> getUnreadCommentsByCourseId(String courseId) {
    	return commentRepository.findUnreadCommentsByCourseId(courseId);
    }

    public CommentsOfChapterInLessonOfCourseResponse getStructuredUnreadComments(String courseId) {
        List<FlatCommentInfo> flatList = commentRepository.findStructuredUnreadComments(courseId);

        if (flatList.isEmpty()) return null;

        CommentsOfChapterInLessonOfCourseResponse response = new CommentsOfChapterInLessonOfCourseResponse();
        response.setCourseId(flatList.get(0).getCourseId());
        response.setCourseTitle(flatList.get(0).getCourseTitle());

        Map<String, CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> lessonMap = new LinkedHashMap<>();

        int totalCommentsOfCourse = 0;

        for (FlatCommentInfo flat : flatList) {
            totalCommentsOfCourse++;

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
                        0, // totalComments tạm thời là 0
                        0,
                        new ArrayList<>()
                    );
                    chapterList.add(ch);
                    return ch;
                });

            chapter.getComments().add(new CommentsOfChapterInLessonOfCourseResponse.CommentResponse(
                flat.getUsername(),
                flat.getDetail(),
                flat.getCreatedDate()
            ));

            chapter.setTotalCommentsOfChapter(chapter.getTotalCommentsOfChapter() + 1);
        }

        List<CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters> sortedLessons = new ArrayList<>(lessonMap.values());
        sortedLessons.sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.LessonWithChapters::getLessonOrder));
        for (var lesson : sortedLessons) {
            lesson.getChapters().sort(Comparator.comparing(CommentsOfChapterInLessonOfCourseResponse.ChapterWithComments::getChapterOrder));
        }

        response.setLessons(sortedLessons);
        response.setTotalCommentsOfCourse(totalCommentsOfCourse);

        return response;
    }

    @Transactional
    public Comment saveCommentWithReadStatusAndNotification(Comment comment) {
        // Bước 1: Gắn ngày tạo
        comment.setCreatedDate(OffsetDateTime.now());

        // Bước 2: Lưu comment trước để có ID dùng cho liên kết
        Comment savedComment = commentRepository.save(comment);

        // Bước 3: Lấy chapter từ comment (giả sử bạn có comment.setChapter(chapter) từ trước)
        Chapter chapter = savedComment.getChapter();
        if (chapter == null) {
            throw new IllegalArgumentException("Comment không chứa thông tin chapter.");
        }

        // Bước 4: Lấy lesson chứa chapter
        Lesson lesson = chapter.getLesson();
        if (lesson == null) {
            throw new IllegalArgumentException("Chapter không thuộc lesson nào.");
        }

        // Bước 5: Tìm tất cả studentId thỏa điều kiện:
        //  - đã hoàn thành lesson
        //  - hoặc đã hoàn thành chapter

        List<Student> eligibleStudents = findEligibleStudents(lesson.getId(), chapter.getId());

        List<CommentReadStatus> readStatuses = new ArrayList<>();
        List<CommentNotification> notifications = new ArrayList<>();

        for (Student student : eligibleStudents) {
            Account account = student.getAccount(); // giả sử có mối quan hệ giữa student và account

            readStatuses.add(CommentReadStatus.builder()
                .account(account)
                .comment(savedComment)
                .isRead(false)
                .build()
            );

            notifications.add(CommentNotification.builder()
                .account(account)
                .chapter(chapter)
                .message("Bạn có bình luận mới.")
                .isRead(false)
                .createdDate(new Date())
                .build()
            );
        }

        commentReadStatusRepository.saveAll(readStatuses);
        commentNotificationRepository.saveAll(notifications);

        return savedComment;
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
