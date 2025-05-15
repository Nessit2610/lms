package com.husc.lms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.ChatMessageNotificationResponse;
import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.dto.response.NotificationResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.Notification.NotificationBuilder;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.service.OffsetLimitPageRequest;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.entity.Course;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
        private final StudentCourseRepository studentCourseRepository;
        private final StudentLessonChapterProgressRepository studentLessonchapterProgressRepository;
        private final AccountRepository accountRepository;
        private final NotificationRepository notificationRepository;
        private final SimpMessagingTemplate messagingTemplate;

        public CommentNotificationResponse getAllUnreadCommentNotificationOfAccount() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();

                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new RuntimeException("Account not found"));

                // Lấy tất cả notification COMMENT của người dùng
                List<Notification> notifications = notificationRepository
                                .findByAccountAndType(account, NotificationType.COMMENT);

                // Đếm số lượng chưa đọc
                int unreadCount = (int) notifications.stream()
                                .filter(notification -> !notification.isRead())
                                .count();

                return CommentNotificationResponse.builder()
                                .notifications(notifications)
                                .totalCount(notifications.size())
                                .unreadCount(unreadCount)
                                .build();
        }

        public void setNotificationAsReadByAccount(List<String> notificationIds) {
                if (notificationIds == null || notificationIds.isEmpty()) {
                        return;
                }
                notificationRepository.setNotificationAsReadByAccount(notificationIds);
        }

        public void markCommentNotificationsAsRead(List<NotificationRequest> notificationRequests) {
                if (notificationRequests == null || notificationRequests.isEmpty()) {
                        return;
                }
                List<String> notificationIds = notificationRequests.stream()
                                .map(NotificationRequest::getId)
                                .collect(Collectors.toList());
                if (!notificationIds.isEmpty()) {
                        notificationRepository.setNotificationAsReadByAccount(notificationIds);
                }
        }

        public NotificationResponse getNotificationsByAccount(int pageNumber, int pageSize) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new RuntimeException("Account not found for username: " + username));

                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }

                Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
                Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

                Page<Notification> notificationPage = notificationRepository.findByAccount(account, pageable);

                List<NotificationResponse.NotificationDetail> notificationDetails = notificationPage.getContent()
                                .stream()
                                .map(notification -> NotificationResponse.NotificationDetail.builder()
                                                .notificationId(notification.getId())
                                                .receivedAccountId(notification.getAccount().getId())
                                                .commentId(notification.getComment() != null
                                                                ? notification.getComment().getId()
                                                                : null)
                                                .commentReplyId(notification.getCommentReply() != null
                                                                ? notification.getCommentReply().getId()
                                                                : null)
                                                .notificationType(notification.getType().name())
                                                .isRead(notification.isRead())
                                                .description(notification.getDescription())
                                                .createdAt(notification.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                Integer unreadCount = notificationRepository.countByAccountAndIsReadFalse(account);

                return NotificationResponse.builder()
                                .notificationDetails(notificationDetails)
                                .countUnreadNotification(unreadCount)
                                .build();
        }

        public void sendCustomWebSocketNotificationToUser(String targetUsername, Map<String, Object> payload) {
                if (targetUsername == null || targetUsername.isEmpty()) {
                        System.err.println("sendCustomWebSocketNotificationToUser: targetUsername không được rỗng.");
                        return;
                }
                if (payload == null || payload.isEmpty()) {
                        System.err.println("sendCustomWebSocketNotificationToUser: payload không được rỗng.");
                        return;
                }

                String destination = "/topic/notifications/" + targetUsername;
                System.out.println("[NotificationService] Sending WebSocket notification to: " + destination
                                + " with payload: " + payload);
                messagingTemplate.convertAndSend(destination, payload);
                
        }

        public void sendConstructedCommentReplyNotification(String targetUsername, Account ownerOfParentComment,
                        CommentReply savedReply, Chapter chapter, Course course, Comment parentComment) {

                if (targetUsername == null || targetUsername.isEmpty()) {
                        System.err.println("sendConstructedCommentReplyNotification: targetUsername không được rỗng.");
                        return;
                }
                if (ownerOfParentComment == null || savedReply == null || chapter == null || course == null
                                || parentComment == null) {
                        System.err.println(
                                        "sendConstructedCommentReplyNotification: Một trong các tham số để tạo payload là null.");
                        return;
                }

                String messageText;
                
                if (parentComment.getAccount().getId().equals(ownerOfParentComment.getId())) {
                        messageText = "Người dùng " + ownerOfParentComment.getUsername()
                                        + " đã trả lời bình luận của bạn: "
                                        + savedReply.getDetail();
                } else {
                        
                        messageText = savedReply.getDetail();
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("message", messageText);
                payload.put("chapterId", chapter.getId());
                payload.put("courseId", course.getId());
                payload.put("parentCommentId", parentComment.getId());
                payload.put("commentReplyId", savedReply.getId());
                payload.put("createdDate", new Date()); // Matches existing logic

                this.sendCustomWebSocketNotificationToUser(targetUsername, payload);
        }
}
