package com.husc.lms.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Comment;
import com.husc.lms.entity.Lesson;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String>{
    List<Comment> findByLessonIdOrderByCreatedDateDesc(Lesson lesson);
}