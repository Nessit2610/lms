package com.husc.lms.controller;

import java.time.OffsetDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.CourseRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.service.CommentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentWebSocketController {
	private final CommentService commentService;
	private final AccountRepository accountRepository;
	private final ChapterRepository chapterRepository;
	private final CourseRepository courseRepository;

	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public CommentMessage handleComment(CommentMessage message) {
	    try {
	        Account account = accountRepository.findByUsername(message.getAccountId()).orElseThrow();
	        Chapter chapter = chapterRepository.findById(message.getChapterId()).orElseThrow();
	        Course course = courseRepository.findById(message.getCourseId()).orElseThrow();

	        Comment comment = Comment.builder()
	                .account(account)
	                .chapter(chapter)
	                .course(course)
	                .detail(message.getDetail())
	                .createdDate(OffsetDateTime.now())
	                .build();

	        commentService.saveComment(comment);

	        // Trả lại lại dữ liệu để client sử dụng
	        return message;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


}
