package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentReplyMessage;
import com.husc.lms.dto.request.CommentReplyUpdateMessage;
import com.husc.lms.dto.response.CommentReplyResponse;
import com.husc.lms.dto.response.CommentReplyUpdateMessageResponse;
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
import com.husc.lms.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentReplyService {

        private final CommentRepository commentRepository;
        private final CommentReplyRepository commentReplyRepository;
        private final ChapterRepository chapterRepository;
        private final CourseRepository courseRepository;
        private final AccountRepository accountRepository;
        private final CommentReadStatusRepository commentReadStatusRepository;
        private final NotificationRepository notificationRepository;

        private final StudentService studentService;
        private final NotificationService notificationService;

        @Transactional
        public CommentReplyResponse saveCommentReplyWithReadStatusAndNotification(CommentReplyMessage message) {
                System.out.println("Owner Username: " + message.getOwnerUsername());
                System.out.println("Reply Username: " + message.getReplyUsername());
                System.out.println("Chapter ID: " + message.getChapterId());
                System.out.println("Course ID: " + message.getCourseId());
                System.out.println("Parent Comment ID: " + message.getParentCommentId());
                System.out.println("Detail: " + message.getDetail());
                // Lấy các entity cần thiết
                Account ownerAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getOwnerUsername())
                                .orElseThrow(() -> new RuntimeException("Owner account not found"));
                Account replyAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getReplyUsername())
                                .orElseThrow(() -> new RuntimeException("Reply account not found"));
                Chapter chapter = chapterRepository.findById(message.getChapterId())
                                .orElseThrow(() -> new RuntimeException("Chapter not found"));
                Course course = courseRepository.findById(message.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found"));
                Comment parentComment = commentRepository.findById(message.getParentCommentId())
                                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
                Lesson lesson = chapter.getLesson();
                if (lesson == null)
                        throw new IllegalArgumentException("Chapter không thuộc lesson nào.");

                // Tạo và lưu CommentReply
                CommentReply commentReply = CommentReply.builder()
                                .ownerAccount(ownerAccount)
                                .replyAccount(replyAccount)
                                .chapter(chapter)
                                .course(course)
                                .detail(message.getDetail())
                                .createdDate(OffsetDateTime.now())
                                .comment(parentComment)
                                .build();
                CommentReply savedReply = commentReplyRepository.save(commentReply);

                int newReplyCountAfterSave = commentReplyRepository.countByComment(parentComment);

                // Gửi CommentReadStatus + Notification cho giáo viên
                Account teacherAccount = course.getTeacher().getAccount();
                if (teacherAccount != null) {
                        commentReadStatusRepository.save(CommentReadStatus.builder()
                                        .account(teacherAccount)
                                        .commentReply(savedReply)
                                        .commentType(CommentType.REPLY)
                                        .isRead(false)
                                        .build());

                        notificationRepository.save(Notification.builder()
                                        .account(teacherAccount)
                                        .commentReply(savedReply)
                                        .description(savedReply.getDetail())
                                        .createdAt(OffsetDateTime.now())
                                        .type(NotificationType.COMMENT_REPLY)
                                        .isRead(false)
                                        .build());

                        // Gửi thông báo riêng cho teacher qua WebSocket
                        Map<String, Object> teacherPayload = new HashMap<>();
                        teacherPayload.put("message",
                                        "Có trả lời mới cho bình luận trong khóa học " + course.getName() + ": "
                                                        + savedReply.getDetail());
                        teacherPayload.put("type", NotificationType.COMMENT_REPLY.name());
                        teacherPayload.put("courseId", course.getId());
                        teacherPayload.put("lessonId", lesson.getId());
                        teacherPayload.put("chapterId", chapter.getId());
                        teacherPayload.put("parentCommentId", parentComment.getId());
                        teacherPayload.put("commentReplyId", savedReply.getId());
                        teacherPayload.put("createdDate", new Date());

                        System.out.println("[CommentReplyService] Sending WebSocket notification to TEACHER: "
                                        + teacherAccount.getUsername() + " with payload: " + teacherPayload);
                        notificationService.sendCustomWebSocketNotificationToUser(teacherAccount.getUsername(),
                                        teacherPayload);
                }

                // Gửi notification cho người được reply (nếu khác người reply)
                if (!replyAccount.getId().equals(ownerAccount.getId())) {
                        notificationRepository.save(Notification.builder()
                                        .account(replyAccount)
                                        .commentReply(savedReply)
                                        .type(NotificationType.COMMENT_REPLY)
                                        .description(message.getDetail())
                                        .isRead(false)
                                        .createdAt(OffsetDateTime.now())
                                        .build());

                        notificationService.sendConstructedCommentReplyNotification(replyAccount.getUsername(),
                                        ownerAccount, savedReply, chapter, course, parentComment);
                }

                // Gửi notification cho các học sinh đủ điều kiện (trừ người reply)
                List<Student> eligibleStudents = studentService.findEligibleStudents(lesson.getId(), chapter.getId());
                for (Student student : eligibleStudents) {
                        Account studentAccount = student.getAccount();
                        if (studentAccount != null && !studentAccount.getId().equals(replyAccount.getId())) {
                                notificationRepository.save(Notification.builder()
                                                .account(studentAccount)
                                                .commentReply(savedReply)
                                                .type(NotificationType.COMMENT_REPLY)
                                                .description(message.getDetail())
                                                .isRead(false)
                                                .createdAt(OffsetDateTime.now())
                                                .build());

                                notificationService.sendConstructedCommentReplyNotification(
                                                studentAccount.getUsername(), ownerAccount, savedReply, chapter, course,
                                                parentComment);
                        }
                }

                // Lấy tên và avatar người reply
                String fullnameReply = "";
                String avatarReply = "";
                if (replyAccount.getStudent() != null) {
                        fullnameReply = replyAccount.getStudent().getFullName();
                        avatarReply = replyAccount.getStudent().getAvatar();
                } else if (replyAccount.getTeacher() != null) {
                        fullnameReply = replyAccount.getTeacher().getFullName();
                        avatarReply = replyAccount.getTeacher().getAvatar();
                }

                // Lấy tên người được reply
                String fullnameOwner = "";
                if (ownerAccount.getStudent() != null) {
                        fullnameOwner = ownerAccount.getStudent().getFullName();
                } else if (ownerAccount.getTeacher() != null) {
                        fullnameOwner = ownerAccount.getTeacher().getFullName();
                }

                // Trả về response
                return CommentReplyResponse.builder()
                                .commentId(savedReply.getComment().getId())
                                .commentReplyId(savedReply.getId())
                                .usernameOwner(ownerAccount.getUsername())
                                .fullnameOwner(fullnameOwner)
                                .usernameReply(replyAccount.getUsername())
                                .fullnameReply(fullnameReply)
                                .avatarReply(avatarReply)
                                .detail(savedReply.getDetail())
                                .createdDate(savedReply.getCreatedDate())
                                .updateDate(savedReply.getUpdateDateAt())
                                .replyCount(newReplyCountAfterSave)
                                .build();
        }

        public CommentReplyUpdateMessageResponse updateCommentReply(CommentReplyUpdateMessage message) {
                CommentReply changeCommentReply = commentReplyRepository.findById(message.getCommentReplyId())
                                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

                Account replyAccount = accountRepository.findByUsernameAndDeletedDateIsNull(message.getUsernameReply())
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

                // Check if the user attempting the update is the actual author of the reply
                if (!changeCommentReply.getReplyAccount().getId().equals(replyAccount.getId())) {
                        throw new AppException(ErrorCode.OWNER_NOT_MATCH); // hoặc tạo error code phù hợp
                }

                changeCommentReply.setDetail(message.getNewDetail());
                changeCommentReply.setUpdateDateAt(OffsetDateTime.now());
                commentReplyRepository.save(changeCommentReply);

                return CommentReplyUpdateMessageResponse.builder()
                                .parentCommentId(changeCommentReply.getId())
                                .usernameOwner(changeCommentReply.getOwnerAccount().getUsername())
                                .fullnameOwner(changeCommentReply.getOwnerAccount().getStudent() != null
                                                ? changeCommentReply.getOwnerAccount().getStudent().getFullName()
                                                : changeCommentReply.getOwnerAccount().getTeacher() != null
                                                                ? changeCommentReply.getOwnerAccount().getTeacher()
                                                                                .getFullName()
                                                                : null)
                                .commentReplyId(changeCommentReply.getId())
                                .usernameReply(changeCommentReply.getReplyAccount().getUsername())
                                .fullnameReply(changeCommentReply.getReplyAccount().getStudent() != null
                                                ? changeCommentReply.getReplyAccount().getStudent().getFullName()
                                                : changeCommentReply.getReplyAccount().getTeacher() != null
                                                                ? changeCommentReply.getReplyAccount().getTeacher()
                                                                                .getFullName()
                                                                : null)
                                .newDetail(changeCommentReply.getDetail())
                                .updateDate(changeCommentReply.getUpdateDateAt())
                                .build();
        }

        @Transactional
        public CommentReplyResponse deleteCommentReply(CommentReplyUpdateMessage message) {
                CommentReply commentReplyToDelete = commentReplyRepository.findById(message.getCommentReplyId())
                                .filter(reply -> reply.getDeletedDate() == null)
                                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

                Account requestingAccount = accountRepository
                                .findByUsernameAndDeletedDateIsNull(message.getUsernameReply())
                                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

                if (!commentReplyToDelete.getReplyAccount().getId().equals(requestingAccount.getId())) {
                        throw new AppException(ErrorCode.OWNER_NOT_MATCH);
                }

                String deletedReplyId = commentReplyToDelete.getId();
                String detailOfDeletedReply = commentReplyToDelete.getDetail();
                OffsetDateTime createdDateOfDeletedReply = commentReplyToDelete.getCreatedDate();

                Account ownerAccountOfParentComment = commentReplyToDelete.getComment().getAccount();
                Account replyAccountOfDeletedReply = commentReplyToDelete.getReplyAccount();

                commentReplyToDelete.setDeletedBy(message.getUsernameReply());
                commentReplyToDelete.setDeletedDate(OffsetDateTime.now());
                commentReplyRepository.save(commentReplyToDelete);

                Comment parentComment = commentReplyToDelete.getComment();
                int newReplyCountForParent = 0;
                String parentCommentIdStr = null;

                if (parentComment != null) {
                        parentCommentIdStr = parentComment.getId();
                        newReplyCountForParent = commentReplyRepository
                                        .countByComment(parentComment);
                }

                String fullnameReply = "";
                String avatarReply = "";
                if (replyAccountOfDeletedReply.getStudent() != null) {
                        fullnameReply = replyAccountOfDeletedReply.getStudent().getFullName();
                        avatarReply = replyAccountOfDeletedReply.getStudent().getAvatar();
                } else if (replyAccountOfDeletedReply.getTeacher() != null) {
                        fullnameReply = replyAccountOfDeletedReply.getTeacher().getFullName();
                        avatarReply = replyAccountOfDeletedReply.getTeacher().getAvatar();
                }

                String fullnameOwner = "";
                if (ownerAccountOfParentComment.getStudent() != null) {
                        fullnameOwner = ownerAccountOfParentComment.getStudent().getFullName();
                } else if (ownerAccountOfParentComment.getTeacher() != null) {
                        fullnameOwner = ownerAccountOfParentComment.getTeacher().getFullName();
                }

                return CommentReplyResponse.builder()
                                .commentId(parentCommentIdStr)
                                .commentReplyId(deletedReplyId)
                                .usernameOwner(ownerAccountOfParentComment.getUsername())
                                .fullnameOwner(fullnameOwner)
                                .usernameReply(replyAccountOfDeletedReply.getUsername())
                                .fullnameReply(fullnameReply)
                                .avatarReply(avatarReply)
                                .detail(detailOfDeletedReply)
                                .createdDate(createdDateOfDeletedReply)
                                .replyCount(newReplyCountForParent)
                                .build();
        }

}
