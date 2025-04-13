package com.husc.lms.controller;

import java.time.OffsetDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;
	private final AccountRepository accountRepository;
	private final LessonRepository lessonRepository;
	private final CourseRepository courseRepository;

	@MessageMapping("/comment")
    @SendTo("/topic/comments")
    public Comment handleComment(CommentMessage message) {
        try {
            System.out.println("Received message: " + message);

            // Lấy entity từ ID
            Account account = accountRepository.findById(message.getAccountId()).orElseThrow();
            Lesson lesson = lessonRepository.findById(message.getLessonId()).orElseThrow();
            Course course = courseRepository.findById(message.getCourseId()).orElseThrow();

            // Tạo đối tượng Comment
            Comment comment = Comment.builder()
                    .account(null)
                    .lesson(lesson)
                    .course(course)
                    .detail(message.getDetail())
                    .createdDate(OffsetDateTime.now())
                    .build();

            // Lưu và trả về comment đã lưu
            return commentService.saveComment(comment);
        } catch (Exception e) {
            System.out.println("Error handling comment message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
