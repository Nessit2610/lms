package com.husc.lms.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Lesson;
import com.husc.lms.repository.CommentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        comment.setCreatedDate(OffsetDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByChapter(Chapter chapter) {
        return commentRepository.findByChapterOrderByCreatedDateDesc(chapter);
    }
}
