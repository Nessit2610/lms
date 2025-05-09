package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.ChatMessageNotificationDto;
import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Notification;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;

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

    public List<ChatMessageNotificationDto> getUnreadChatMessageNotificationsForCurrentUser() {
        // Implementation of the method
        return null; // Placeholder return, actual implementation needed
    }

    public Notification createNotificationForChatMessage(String recipientUsername, String senderDisplayName,
            String chatBoxName, String chatMessageId, String chatBoxId, String shortContent) {
        // Implementation of the method
        return null; // Placeholder return, actual implementation needed
    }

    public Notification createAndSendChatMessageNotification(
            String recipientUsername,
            String senderDisplayName,
            String chatBoxName, // or chatBoxId to derive name
            String chatMessageId,
            String chatBoxId,
            String shortContent, // e.g., first few words of the message
            boolean isRecipientActiveInBox // New parameter
    ) {
        // Implementation of the method
        return null; // Placeholder return, actual implementation needed
    }

    public Notification createChatMessageNotificationLogic(
            String recipientUsername,
            String senderDisplayName,
            String chatBoxName,
            String chatMessageId,
            String chatBoxId,
            String shortContent) {
        // Implementation of the method
        return null; // Placeholder return, actual implementation needed
    }

}
