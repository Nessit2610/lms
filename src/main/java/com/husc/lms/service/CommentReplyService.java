package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentReplyMessage;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentReadStatusRepository;
import com.husc.lms.repository.CommentReplyRepository;
import com.husc.lms.repository.CommentRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

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
	private final StudentRepository studentRepository;
	private final CommentReadStatusRepository commentReadStatusRepository;
	private final StudentLessonChapterProgressRepository studentLessonChapterProgressRepository;
	private final StudentLessonProgressRepository studentLessonProgressRepository;
	private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
	
    @Transactional
    public void saveCommentReplyWithReadStatusAndNotification(CommentReplyMessage message) {
        // Lấy các entity cần thiết
        Account ownerAccount = accountRepository.findByUsername(message.getOwnerAccountId())
            .orElseThrow(() -> new RuntimeException("Owner account not found"));

        Account replyAccount = accountRepository.findByUsername(message.getReplyAccountId())
            .orElseThrow(() -> new RuntimeException("Reply account not found"));

        Chapter chapter = chapterRepository.findById(message.getChapterId())
            .orElseThrow(() -> new RuntimeException("Chapter not found"));

        Course course = courseRepository.findById(message.getCourseId())
            .orElseThrow(() -> new RuntimeException("Course not found"));

        // Sử dụng parentCommentId thay vì commentId
        Comment parentComment = commentRepository.findById(message.getParentCommentId())
            .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        // Tạo đối tượng CommentReply
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

        // Tạo thông báo nếu người được reply khác với người reply
        if (!replyAccount.getId().equals(ownerAccount.getId())) {
            Notification notification = Notification.builder()
                .account(replyAccount)
                .commentReply(savedReply)
                .type(NotificationType.COMMENT_REPLY)
                .description(message.getDetail())
                .isRead(false)
                .createdAt(new Date())
                .build();

            notificationRepository.save(notification);

            // Gửi real-time notification
            String destination = "/topic/notifications/" + replyAccount.getUsername();
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", notification.getDescription());
            payload.put("chapterId", chapter.getId());
            payload.put("createdDate", notification.getCreatedAt());
            messagingTemplate.convertAndSend(destination, payload);
        }
    }
}
