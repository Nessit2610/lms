package com.husc.lms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
        private final StudentCourseRepository studentCourseRepository;
        private final StudentLessonChapterProgressRepository studentLessonchapterProgressRepository;
        private final AccountRepository accountRepository;
        private final NotificationRepository notificationRepository;

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
}
