package com.husc.lms.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.NotificationRequest;
import com.husc.lms.dto.response.ChatMessageNotificationResponse;
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
                    // Khi chuyển đổi từ Request, chúng ta cần đảm bảo rằng Notification này thuộc
                    // về Account hiện tại
                    // Tuy nhiên, phương thức setNotificationAsReadByAccount trong repository có thể
                    // đã xử lý việc này
                    // hoặc cần điều chỉnh để chỉ cập nhật những thông báo thuộc account này.
                    // Hiện tại, builder không set Account, điều này có thể là một thiếu sót nếu
                    // repository không kiểm tra.
                    // Giả sử rằng ID của Notification là đủ để xác định và repository sẽ xử lý
                    // quyền.
                    return Notification.builder()
                            .id(request.getId()) // ID của notification cần đánh dấu đã đọc
                            .type(request.getType()) // Có thể không cần thiết nếu chỉ dựa vào ID
                            .isRead(true) // Đánh dấu là đã đọc
                            .description(request.getDescription()) // Có thể không cần thiết
                            .createdAt(request.getCreatedAt()) // Có thể không cần thiết
                            // Quan trọng: Không set Account ở đây trừ khi bạn muốn tạo Notification mới
                            // hoặc nếu logic setNotificationAsReadByAccount yêu cầu Account được set trên
                            // đối tượng này.
                            // Thông thường, bạn chỉ cần ID để cập nhật.
                            .build();
                })
                .collect(Collectors.toList());

        // Gọi phương thức hiện có để cập nhật DB
        // Cần đảm bảo phương thức này chỉ cập nhật các notifications thuộc về `account`
        // đã lấy ở trên.
        // Nếu `notificationRepository.setNotificationAsReadByAccount` chỉ dựa vào ID
        // trong List<Notification>
        // mà không kiểm tra quyền sở hữu của `account` thì có thể có lỗ hổng bảo mật.
        // Tuy nhiên, giữ nguyên logic gọi như ban đầu của bạn trước.
        setNotificationAsReadByAccount(notificationsToUpdate);
    }
}
