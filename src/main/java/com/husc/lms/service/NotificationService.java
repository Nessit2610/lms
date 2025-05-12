package com.husc.lms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

        public void setNotificationAsReadByAccount(List<Notification> notifications) {
                if (notifications == null || notifications.isEmpty()) {
                        // Nếu không có thông báo thì không làm gì cả
                        return;
                }

                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new RuntimeException("Account not found"));

                // Truyền cả account và danh sách notifications để xử lý chính xác
                notificationRepository.setNotificationAsReadByAccount(notifications);
        }

        public void markCommentNotificationsAsRead(List<NotificationRequest> notificationRequests) {
                if (notificationRequests == null || notificationRequests.isEmpty()) {
                        return; // Không có gì để xử lý
                }

                // Lấy thông tin tài khoản hiện tại (logic này có thể được tái cấu trúc nếu cần
                // dùng ở nhiều nơi)
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new RuntimeException("Account not found for username: " + username));

                List<Notification> notificationsToUpdate = notificationRequests.stream()
                                .map(request -> {
                                        NotificationBuilder notificationBuilder = Notification.builder()
                                                        .id(request.getId()) // ID của notification cần đánh dấu đã đọc
                                                        .type(request.getType())
                                                        .isRead(true) // Đánh dấu là đã đọc
                                                        .description(request.getDescription());

                                        if (request.getCreatedAt() != null) {
                                                notificationBuilder.createdAt(request.getCreatedAt());
                                        }
                                        return notificationBuilder.build();
                                })
                                .collect(Collectors.toList());

                setNotificationAsReadByAccount(notificationsToUpdate);
        }

        public Page<NotificationResponse> getNotificationsByAccount(int pageNumber, int pageSize) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                                .orElseThrow(() -> new RuntimeException("Account not found"));

                if (pageSize < 1) {
                        throw new IllegalArgumentException("pageSize must be 1 or greater.");
                }

                int actualOffset = pageNumber;
                int actualLimit = pageSize + 1;
                Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

                Pageable fetchPageable = new OffsetLimitPageRequest(actualOffset, actualLimit, sort);

                Page<Notification> fetchedNotificationsPage = notificationRepository.findByAccount(account,
                                fetchPageable);

                List<Notification> notifications = fetchedNotificationsPage.getContent();
                boolean hasNext = notifications.size() > pageSize;

                List<Notification> notificationsToReturn = hasNext ? notifications.subList(0, pageSize) : notifications;

                Pageable returnPageable = PageRequest.of(pageNumber / pageSize, pageSize, sort);

                Page<Notification> finalNotificationPage = new PageImpl<>(notificationsToReturn, returnPageable,
                                fetchedNotificationsPage.getTotalElements());

                return finalNotificationPage.map(notification -> NotificationResponse.builder()
                                .notificationId(notification.getId())
                                .receivedAccountId(notification.getAccount().getId())
                                .commentId(notification.getComment() != null ? notification.getComment().getId() : null)
                                .commentReplyId(notification.getCommentReply() != null
                                                ? notification.getCommentReply().getId()
                                                : null)
                                .notificationType(notification.getType().name())
                                .isRead(notification.isRead())
                                .description(notification.getDescription())
                                .createdAt(notification.getCreatedAt())
                                .build());
        }
}
