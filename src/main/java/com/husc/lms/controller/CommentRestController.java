package com.husc.lms.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.husc.lms.dto.response.CommentChapterResponse;
import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.ChapterRepository;
import com.husc.lms.repository.LessonRepository;
import com.husc.lms.service.CommentService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService commentService;
	private final ChapterRepository chapterRepository;
	
	
	@PostMapping
	public ResponseEntity<Comment> addComment(@RequestBody Comment comment){
		return ResponseEntity.ok(commentService.saveComment(comment));
	}
	
	@GetMapping("/chapter/{chapterId}")
	public ResponseEntity<List<CommentChapterResponse>> getComments(@PathVariable("chapterId") String chapterId) {
	    Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
	    List<Comment> comments = commentService.getCommentsByChapter(chapter);

	    List<CommentChapterResponse> response = comments.stream()
	            .map(comment -> new CommentChapterResponse(
	                    comment.getAccount().getUsername(),
	                    comment.getDetail(),
	                    comment.getCreatedDate()
	            ))
	            .collect(Collectors.toList());

	    return ResponseEntity.ok(response);
	}

}
