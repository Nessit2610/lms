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

        // Define OffsetLimitPageRequest as a private static inner class
        private static class OffsetLimitPageRequest implements Pageable {
                private final int offset;
                private final int limit;
                private final Sort sort;

                public OffsetLimitPageRequest(int offset, int limit, Sort sort) {
                        if (offset < 0) {
                                throw new IllegalArgumentException("Offset index must not be less than zero!");
                        }
                        if (limit < 1) {
                                throw new IllegalArgumentException("Limit must not be less than one!");
                        }
                        this.offset = offset;
                        this.limit = limit;
                        this.sort = sort;
                }

                @Override
                public int getPageNumber() {
                        // Represents the "page number" if data were divided into chunks of 'limit'
                        return offset / limit;
                }

                @Override
                public int getPageSize() {
                        return limit;
                }

                @Override
                public long getOffset() {
                        return offset;
                }

                @Override
                public Sort getSort() {
                        return sort;
                }

                @Override
                public Pageable next() {
                        return new OffsetLimitPageRequest(offset + limit, limit, sort);
                }

                @Override
                public Pageable previousOrFirst() {
                        return (offset - limit >= 0) ? new OffsetLimitPageRequest(offset - limit, limit, sort)
                                        : first();
                }

                @Override
                public Pageable first() {
                        return new OffsetLimitPageRequest(0, limit, sort);
                }

                @Override
                public boolean hasPrevious() {
                        return offset > 0;
                }

                @Override
                public Pageable withPage(int pageNumber) {
                        if (pageNumber < 0) {
                                throw new IllegalArgumentException("Page number must not be less than zero!");
                        }
                        return new OffsetLimitPageRequest(pageNumber * limit, limit, sort);
                }

                @Override
                public boolean equals(Object o) {
                        if (this == o)
                                return true;
                        if (!(o instanceof OffsetLimitPageRequest))
                                return false;
                        OffsetLimitPageRequest that = (OffsetLimitPageRequest) o;
                        return offset == that.offset && limit == that.limit
                                        && java.util.Objects.equals(sort, that.sort);
                }

                @Override
                public int hashCode() {
                        return java.util.Objects.hash(offset, limit, sort);
                }
        }

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

        public Page<CommentChapterResponse> getCommentsByChapterId(String chapterId, int pageNumber, int pageSize) {
                Chapter chapter = chapterRepository.findById(chapterId)
                                .orElseThrow(() -> new RuntimeException("Chapter not found"));

                if (pageSize < 0) {
                        throw new IllegalArgumentException("pageSize must be non-negative.");
                }

                int actualOffset = pageNumber * pageSize; // Correct offset calculation
                int actualLimit = pageSize + 1;

                // The sort order is derived from the repository method name convention
                Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
                // Use Spring's PageRequest for offset and limit if your repository supports it
                // directly
                // or adjust OffsetLimitPageRequest if it's a custom implementation for
                // offset/limit.
                // For now, assuming OffsetLimitPageRequest is designed for raw offset and
                // limit.
                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<Comment> fetchedCommentsPage = commentRepository
                                .findByChapterAndDeletedDateIsNullOrderByCreatedDateDesc(
                                                chapter,
                                                fetchPageable);

                List<Comment> comments = fetchedCommentsPage.getContent();
                boolean hasNext = comments.size() > pageSize;

                List<Comment> commentsToReturn = hasNext ? comments.subList(0, pageSize) : comments;

                // Create a new Pageable for the returned Page, reflecting the requested
                // pageSize
                Pageable returnPageable = PageRequest.of(pageNumber, pageSize, sort);

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

                if (pageSize < 0) {
                        throw new IllegalArgumentException("pageSize must be non-negative.");
                }

                int actualOffset = pageNumber * pageSize; // Correct offset calculation
                int actualLimit = pageSize + 1;
                Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
                // Assuming OffsetLimitPageRequest is designed for raw offset and limit.
                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<CommentReply> fetchedRepliesPage = commentReplyRepository.findByComment(comment, fetchPageable);

                List<CommentReply> replies = fetchedRepliesPage.getContent();
                boolean hasNext = replies.size() > pageSize;

                List<CommentReply> repliesToReturn = hasNext ? replies.subList(0, pageSize) : replies;

                // Create a new Pageable for the returned Page, reflecting the requested
                // pageSize
                Pageable returnPageable = PageRequest.of(pageNumber, pageSize, sort);

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
                        } else if (ownerAccount.getTeacher() != null) { // Corrected: was replyAccount.getTeacher()
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
                                        .createdAt(OffsetDateTime.now())
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
                                .fullname(account.getStudent() != null && account.getStudent().getFullName() != null
                                                ? account.getStudent().getFullName()
                                                : (account.getTeacher() != null
                                                                && account.getTeacher().getFullName() != null
                                                                                ? account.getTeacher().getFullName()
                                                                                : ""))
                                .avatar(account.getStudent() != null && account.getStudent().getAvatar() != null
                                                ? account.getStudent().getAvatar()
                                                : (account.getTeacher() != null
                                                                && account.getTeacher().getAvatar() != null
                                                                                ? account.getTeacher().getAvatar()
                                                                                : ""))
                                .detail(comment.getDetail())
                                .createdDate(comment.getCreatedDate())
                                .commentReplyResponses(List.of())
                                .build();

        }

        public List<Student> findEligibleStudents(String lessonId, String chapterId) {
                List<String> studentIdsByLesson = studentLessonProgressRepository
                                .findStudentIdsByLessonCompleted(lessonId);
                List<String> studentIdsByChapter = studentLessonChapterProgressRepository
                                .findStudentIdsByChapterCompleted(chapterId);

                Set<String> uniqueStudentIds = new HashSet<>();
                uniqueStudentIds.addAll(studentIdsByLesson);
                uniqueStudentIds.addAll(studentIdsByChapter);

                return studentRepository.findAllById(uniqueStudentIds);
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

}
