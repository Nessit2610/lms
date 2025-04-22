package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.response.CommentNotificationResponse;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Notification;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.enums.NotificationType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CommentReadStatusRepository;
import com.husc.lms.repository.CommentRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.NotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;
import com.husc.lms.repository.StudentLessonProgressRepository;
import com.husc.lms.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final StudentCourseRepository studentCourseRepository;
    private final StudentLessonChapterProgressRepository studentLessonchapterProgressRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

//    public void notifyStudentsOnNewComment(Chapter chapter, String commentContent) {
//        Course course = chapter.getLesson().getCourse();
//
//        // Bước 1: Lấy tất cả học viên đã đăng ký khóa học
//        List<StudentCourse> studentCourses = studentCourseRepository.findByCourse(course);
//
//        // Bước 2: Kiểm tra ai đã học tới chapter này
//        for (StudentCourse sc : studentCourses) {
//            boolean hasReachedChapter = studentLessonchapterProgressRepository
//                .existsByStudentAndChapter(sc.getStudent(), chapter);  // Cập nhật để kiểm tra accountId
//
//            if (hasReachedChapter) {
////                // Bước 3: Tạo notification
////                CommentNotification notification = CommentNotification.builder()
////                        .account(sc.getStudent().getAccount())  // Dùng account thay vì student
////                        .chapter(chapter)
////                        .message("Có bình luận mới trong chương bạn đã học: " + commentContent)
////                        .isRead(false)
////                        .createdDate(new Date())
////                        .build();
////
////                commentNotificationRepository.save(notification);
//            	if (hasReachedChapter) {
//                    // Bước 3: Tạo notification
//                    Notification notification = Notification.builder()
//                            .receiveAccount(sc.getStudent().getAccount())  // Dùng account thay vì student
//                            .chapter(chapter)
//                            .content("Có bình luận mới trong chương bạn đã học: " + commentContent)
//                            .createdAt(new Date())
//                            .build();
//
//                    notificationRepository.save(notification);
//                }
//            }
//        }
//    }
//    
    public CommentNotificationResponse getAllUnreadCommentNotificationOfAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lấy tất cả notification COMMENT của người dùng
        List<Notification> notifications = notificationRepository
                .findByReceiveAccountAndType(account, NotificationType.COMMENT);

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

}
