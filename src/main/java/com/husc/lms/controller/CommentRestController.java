package com.husc.lms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService commentService;
	private final LessonRepository lessonRepository;
	
	
	@PostMapping
	public ResponseEntity<Comment> addComment(@RequestBody Comment comment){
		return ResponseEntity.ok(commentService.saveComment(comment));
	}
	
	@GetMapping("/lesson/{lessonId}")
	public ResponseEntity<List<Comment>> getComments(@PathVariable("lessonId") String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
		return ResponseEntity.ok(commentService.getCommentsByLesson(lesson));
	}
}
