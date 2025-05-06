package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;

@Repository
public interface CommentReplyRepository  extends JpaRepository<CommentReply, String>{
    Page<CommentReply> findByComment(Comment comment, Pageable pageable);
    List<CommentReply> findByCommentIdIn(List<String> commentIds);
}
