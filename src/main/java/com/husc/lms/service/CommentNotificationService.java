package com.husc.lms.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.CommentNotification;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.StudentCourse;
import com.husc.lms.repository.CommentNotificationRepository;
import com.husc.lms.repository.StudentCourseRepository;
import com.husc.lms.repository.StudentLessonChapterProgressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentNotificationService {
	private final StudentCourseRepository studentCourseRepository;
    private final StudentLessonChapterProgressRepository studentLessonchapterProgressRepository;
    private final CommentNotificationRepository commentNotificationRepository;

    public void notifyStudentsOnNewComment(Chapter chapter, String commentContent) {
        Course course = chapter.getLesson().getCourse();

        // Bước 1: Lấy tất cả học viên đã đăng ký khóa học
        List<StudentCourse> studentCourses = studentCourseRepository.findByCourse(course);

        // Bước 2: Kiểm tra ai đã học tới chapter này
        for (StudentCourse sc : studentCourses) {
            boolean hasReachedChapter = studentLessonchapterProgressRepository
                .existsByStudentAndChapter(sc.getStudent(), chapter);  // Cập nhật để kiểm tra accountId

            if (hasReachedChapter) {
                // Bước 3: Tạo notification
                CommentNotification notification = CommentNotification.builder()
                        .account(sc.getStudent().getAccount())  // Dùng account thay vì student
                        .chapter(chapter)
                        .message("Có bình luận mới trong chương bạn đã học: " + commentContent)
                        .isRead(false)
                        .createdDate(new Date())
                        .build();

                commentNotificationRepository.save(notification);
            }
        }
    }

}
