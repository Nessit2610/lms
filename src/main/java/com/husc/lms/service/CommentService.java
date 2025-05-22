package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.request.CommentUpdateMessage;
import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.dto.response.CommentMessageResponse;
import com.husc.lms.dto.response.CommentOfCourseResponse;
import com.husc.lms.dto.response.CommentPostResponse;
import com.husc.lms.dto.response.CommentReplyResponse;
import com.husc.lms.dto.response.CommentUpdateMessageResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReadStatus;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Post;
import com.husc.lms.entity.Student;
import com.husc.lms.enums.CommentType;
import com.husc.lms.enums.ErrorCode;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.exception.AppException;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentReadStatusRepository;
import com.husc.lms.repository.CommentReplyRepository;
import com.husc.lms.repository.CommentRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.PostRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;

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
        private final LessonRepository lessonRepository;
        private final SimpMessagingTemplate messagingTemplate;
        private final NotificationService notificationService;
        private final PostRepository postRepository;

        public Page<CommentChapterResponse> getCommentsByChapterId(String chapterId, int pageNumber, int pageSize) {
                Chapter chapter = chapterRepository.findById(chapterId)
                                .orElseThrow(() -> new RuntimeException("Chapter not found"));

                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }

                int actualOffset = pageNumber;
                int actualLimit = pageSize + 1;

                Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<Comment> fetchedCommentsPage = commentRepository
                                .findByChapterAndDeletedDateIsNullOrderByCreatedDateDesc(
                                                chapter,
                                                fetchPageable);

                List<Comment> comments = fetchedCommentsPage.getContent();
                boolean hasNext = comments.size() > pageSize;

                List<Comment> commentsToReturn = hasNext ? comments.subList(0, pageSize) : comments;

                Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

                Page<Comment> finalCommentPage = new PageImpl<>(commentsToReturn, returnPageable,
                                fetchedCommentsPage.getTotalElements());

                return finalCommentPage.map(comment -> {
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

                        int countOfReply = commentReplyRepository.countByComment(comment);

                        return CommentChapterResponse.builder()
                                        .commentId(comment.getId())
                                        .username(usernameOwner)
                                        .fullname(fullnameOwner)
                                        .avatar(commentAvatar)
                                        .detail(comment.getDetail())
                                        .createdDate(comment.getCreatedDate())
                                        .updateDate(comment.getUpdateDateAt())
                                        .countOfReply(countOfReply)
                                        .build();
                });
        }

        public Page<CommentReplyResponse> getCommentRepliesByCommentId(String commentId, int pageNumber, int pageSize) {
                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comment not found"));

                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }

                int actualOffset = pageNumber;
                int actualLimit = pageSize + 1;
                Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<CommentReply> fetchedRepliesPage = commentReplyRepository.findByComment(comment, fetchPageable);

                List<CommentReply> replies = fetchedRepliesPage.getContent();
                boolean hasNext = replies.size() > pageSize;

                List<CommentReply> repliesToReturn = hasNext ? replies.subList(0, pageSize) : replies;

                Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

                Page<CommentReply> finalReplyPage = new PageImpl<>(repliesToReturn, returnPageable,
                                fetchedRepliesPage.getTotalElements());

                return finalReplyPage.map(reply -> {
                        Account replyAccount = reply.getReplyAccount();
                        String replyAvatar = "";
                        String replyFullname = "";
                        Account ownerAccount = reply.getOwnerAccount();
                        String fullnameOwner = "";

                        if (replyAccount.getStudent() != null) {
                                replyAvatar = Optional.ofNullable(replyAccount.getStudent().getAvatar()).orElse("");
                                replyFullname = replyAccount.getStudent().getFullName();
                        } else if (replyAccount.getTeacher() != null) {
                                replyAvatar = Optional.ofNullable(replyAccount.getTeacher().getAvatar()).orElse("");
                                replyFullname = replyAccount.getTeacher().getFullName();
                        }

                        if (ownerAccount.getStudent() != null) {
                                fullnameOwner = ownerAccount.getStudent().getFullName();
                        } else if (ownerAccount.getTeacher() != null) {
                                fullnameOwner = ownerAccount.getTeacher().getFullName();
                        }

                        return CommentReplyResponse.builder()
                                        .commentId(reply.getComment().getId())
                                        .commentReplyId(reply.getId())
                                        .usernameReply(replyAccount.getUsername())
                                        .avatarReply(replyAvatar)
                                        .fullnameReply(replyFullname)
                                        .usernameOwner(ownerAccount.getUsername())
                                        .fullnameOwner(fullnameOwner)
                                        .detail(reply.getDetail())
                                        .createdDate(reply.getCreatedDate())
                                        .updateDate(reply.getUpdateDateAt())
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

        public Page<CommentOfCourseResponse> getCoursesWithUnreadComments(int pageNumber, int pageSize) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Account teacherAccount = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

                if (teacherAccount.getTeacher() == null) {
                        return Page.empty();
                }

                Pageable pageable = PageRequest.of(pageNumber, pageSize);
                Page<Course> courses = courseRepository.findByTeacherAndDeletedDateIsNull(teacherAccount.getTeacher(),
                                pageable);

                return courses.map(course -> {
                        int total = commentReadStatusRepository.countAllValidCommentReadStatusesByCourse(course)
                                        + commentReadStatusRepository
                                                        .countAllValidCommentReplyReadStatusesByCourse(course);
                        int unread = commentReadStatusRepository.countUnreadValidCommentReadStatusesByCourse(course)
                                        + commentReadStatusRepository
                                                        .countUnreadValidCommentReplyReadStatusesByCourse(course);

                        return CommentOfCourseResponse.builder()
                                        .courseId(course.getId())
                                        .courseName(course.getName())
                                        .totalCommentsOfCourse(total)
                                        .totalUnreadCommentsfCourse(unread)
                                        .build();
                });
        }

        public List<CommentOfCourseResponse.CommentOfLesson> getLessonsWithUnreadComments(String courseId) {
                Course course = courseRepository.findByIdAndDeletedDateIsNull(courseId);

                return course.getLesson().stream().map(lesson -> {
                        int total = commentReadStatusRepository.countAllValidCommentReadStatusesByLessonInCourse(lesson)
                                        + commentReadStatusRepository
                                                        .countAllValidCommentReplyReadStatusesByLessonInCourse(lesson);
                        int unread = commentReadStatusRepository.countUnreadCommentReadStatusesByLessonInCourse(lesson)
                                        + commentReadStatusRepository
                                                        .countUnreadCommentReplyReadStatusesByLessonInCourse(lesson);

                        return CommentOfCourseResponse.CommentOfLesson.builder()
                                        .lessonId(lesson.getId())
                                        .lessonName(lesson.getDescription())
                                        .lessonOrder(lesson.getOrder())
                                        .totalCommentsOfLesson(total)
                                        .totalUnreadCommentsOfLesson(unread)
                                        .build();
                }).toList();
        }

        public List<CommentOfCourseResponse.CommentOfChapter> getChaptersWithUnreadComments(String lessonId) {
                Lesson lesson = lessonRepository.findByIdAndDeletedDateIsNull(lessonId);

                return lesson.getChapter().stream().map(chapter -> {
                        int total = commentReadStatusRepository.countAllValidCommentReadStatusesByChapter(chapter)
                                        + commentReadStatusRepository
                                                        .countAllValidCommentReplyReadStatusesByChapter(chapter);
                        int unread = commentReadStatusRepository.countUnreadCommentReadStatusesByChapter(chapter)
                                        + commentReadStatusRepository
                                                        .countUnreadCommentReplyReadStatusesByChapter(chapter);

                        return CommentOfCourseResponse.CommentOfChapter.builder()
                                        .chapterId(chapter.getId())
                                        .chapterName(chapter.getName())
                                        .chapterOrder(chapter.getOrder())
                                        .totalCommentsOfChapter(total)
                                        .totalUnreadCommentsOfChapter(unread)
                                        .build();
                }).toList();
        }

        @Transactional
        public CommentMessageResponse saveCommentWithReadStatusAndNotification(CommentMessage commentMessage) {
                // Lấy thông tin cần thiết từ message
                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(commentMessage.getUsername())
                                .orElseThrow(() -> new RuntimeException("Account not found"));

                Comment comment;
                boolean isPostComment = commentMessage.getPostId() != null;

                if (isPostComment) {
                        // Xử lý comment của post
                        Post post = postRepository.findById(commentMessage.getPostId())
                                        .orElseThrow(() -> new RuntimeException("Post not found"));

                        comment = Comment.builder()
                                        .account(account)
                                        .post(post)
                                        .detail(commentMessage.getDetail())
                                        .createdDate(OffsetDateTime.now())
                                        .build();
                } else {
                        // Xử lý comment của chapter
                        Chapter chapter = chapterRepository.findById(commentMessage.getChapterId())
                                        .orElseThrow(() -> new RuntimeException("Chapter not found"));
                        Course course = courseRepository.findById(commentMessage.getCourseId())
                                        .orElseThrow(() -> new RuntimeException("Course not found"));

                        comment = Comment.builder()
                                        .account(account)
                                        .chapter(chapter)
                                        .course(course)
                                        .detail(commentMessage.getDetail())
                                        .createdDate(OffsetDateTime.now())
                                        .build();
                }

                Comment savedComment = commentRepository.save(comment);

                // Xử lý notification và read status
                if (isPostComment) {
                        // Xử lý cho post comment
                        Post post = savedComment.getPost();
                        Account postOwner = post.getTeacher().getAccount();

                        if (postOwner != null && !postOwner.getId().equals(account.getId())) {
                                // Tạo CommentReadStatus cho chủ post
                                commentReadStatusRepository.save(CommentReadStatus.builder()
                                                .account(postOwner)
                                                .comment(savedComment)
                                                .commentType(CommentType.COMMENT_POST)
                                                .isRead(false)
                                                .build());

                                // Tạo notification cho chủ post
                                Notification postNotification = Notification.builder()
                                                .account(postOwner)
                                                .comment(savedComment)
                                                .post(post)
                                                .type(NotificationType.POST_COMMENT)
                                                .description(savedComment.getDetail())
                                                .isRead(false)
                                                .createdAt(OffsetDateTime.now())
                                                .build();
                                notificationRepository.save(postNotification);

                                // Gửi WebSocket notification
                                Map<String, Object> payload = new HashMap<>();
                                payload.put("message",
                                                "Có bình luận mới trong bài viết của bạn: " + savedComment.getDetail());
                                payload.put("type", NotificationType.POST_COMMENT.name());
                                payload.put("postId", post.getId());
                                payload.put("commentId", savedComment.getId());
                                payload.put("createdDate", new Date());

                                notificationService.sendCustomWebSocketNotificationToUser(postOwner.getUsername(),
                                                payload);
                        }
                } else {
                        // Xử lý cho chapter comment (giữ nguyên logic cũ)
                        Lesson lesson = savedComment.getChapter().getLesson();
                        if (lesson == null) {
                                throw new IllegalArgumentException("Chapter không thuộc lesson nào.");
                        }

                        Account teacherAccount = savedComment.getCourse().getTeacher().getAccount();
                        if (teacherAccount != null) {
                                boolean isTeacherTheCommenter = teacherAccount.getId().equals(account.getId());

                                commentReadStatusRepository.save(CommentReadStatus.builder()
                                                .account(teacherAccount)
                                                .comment(savedComment)
                                                .commentType(CommentType.COMMENT)
                                                .isRead(isTeacherTheCommenter)
                                                .build());

                                if (!isTeacherTheCommenter) {
                                        Notification teacherNotification = Notification.builder()
                                                        .account(teacherAccount)
                                                        .comment(savedComment)
                                                        .type(NotificationType.COMMENT)
                                                        .description(savedComment.getDetail())
                                                        .isRead(false)
                                                        .createdAt(OffsetDateTime.now())
                                                        .build();
                                        notificationRepository.save(teacherNotification);

                                        Map<String, Object> teacherPayload = new HashMap<>();
                                        teacherPayload.put("message",
                                                        "Có bình luận mới từ khóa học "
                                                                        + savedComment.getCourse().getName() + ": "
                                                                        + savedComment.getDetail());
                                        teacherPayload.put("type", NotificationType.COMMENT.name());
                                        teacherPayload.put("courseId", savedComment.getCourse().getId());
                                        teacherPayload.put("lessonId", lesson.getId());
                                        teacherPayload.put("chapterId", savedComment.getChapter().getId());
                                        teacherPayload.put("commentId", savedComment.getId());
                                        teacherPayload.put("createdDate", new Date());

                                        notificationService.sendCustomWebSocketNotificationToUser(
                                                        teacherAccount.getUsername(), teacherPayload);
                                }
                        }
                }

                // Trả về response
                return CommentMessageResponse.builder()
                                .chapterId(isPostComment ? null : savedComment.getChapter().getId())
                                .courseId(isPostComment ? null : savedComment.getCourse().getId())
                                .postId(isPostComment ? savedComment.getPost().getId() : null)
                                .commentId(savedComment.getId())
                                .username(account.getUsername())
                                .fullname(account.getStudent() != null ? account.getStudent().getFullName()
                                                : account.getTeacher() != null ? account.getTeacher().getFullName()
                                                                : "")
                                .avatar(account.getStudent() != null ? account.getStudent().getAvatar()
                                                : account.getTeacher() != null ? account.getTeacher().getAvatar() : "")
                                .detail(comment.getDetail())
                                .createdDate(comment.getCreatedDate())
                                .commentReplyResponses(List.of())
                                .build();
        }

        public CommentUpdateMessageResponse updateComment(CommentUpdateMessage message) {
                Comment changeComment = commentRepository.findById(message.getCommentId())
                                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

                Account ownerAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getUsernameOwner())
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

                if (!changeComment.getAccount().getId().equals(ownerAccount.getId())) {
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
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

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

        public Page<CommentPostResponse> getCommentsByPost(String postId, int pageSize, int pageNumber) {
        	System.out.println(pageSize);
                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }

                int actualOffset = pageNumber;
                int actualLimit = pageSize + 1;
                Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");

                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<Comment> fetchedCommentsPage = commentRepository.findByPostId(postId, fetchPageable);

                List<Comment> comments = fetchedCommentsPage.getContent();
                boolean hasNext = comments.size() > pageSize;

                List<Comment> commentsToReturn = hasNext ? comments.subList(0, pageSize) : comments;

                Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

                Page<Comment> finalCommentPage = new PageImpl<>(commentsToReturn, returnPageable,
                                fetchedCommentsPage.getTotalElements());

                return finalCommentPage.map(comment -> {
                        Account account = comment.getAccount();
                        return CommentPostResponse.builder()
                                        .commentId(comment.getId())
                                        .username(account.getUsername())
                                        .fullname(account.getStudent() != null ? account.getStudent().getFullName()
                                                        : account.getTeacher() != null
                                                                        ? account.getTeacher().getFullName()
                                                                        : "")
                                        .avatar(account.getStudent() != null ? account.getStudent().getAvatar()
                                                        : account.getTeacher() != null
                                                                        ? account.getTeacher().getAvatar()
                                                                        : "")
                                        .detail(comment.getDetail())
                                        .createdDate(comment.getCreatedDate())
                                        .updateDate(comment.getUpdateDateAt())
                                        .countOfReply(commentReplyRepository.countByComment(comment))
                                        .build();
                });
        }
}
