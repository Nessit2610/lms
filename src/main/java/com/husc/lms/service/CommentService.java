package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.CommentRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        comment.setCreatedDate(OffsetDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByLesson(Lesson lesson) {
        return commentRepository.findByLessonIdOrderByCreatedDateDesc(lesson);
    }
}
