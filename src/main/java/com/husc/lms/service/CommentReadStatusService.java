package com.husc.lms.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Comment;
import com.husc.lms.entity.CommentReply;
import com.husc.lms.enums.CommentType;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CommentReadStatusRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentReadStatusService {
	
    private final CommentReadStatusRepository commentReadStatusRepository;
    private final AccountRepository accountRepository;

	public void setCommentsAsReadByAccount(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            throw new IllegalArgumentException("Comment list hoặc account không được null hoặc rỗng.");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        commentReadStatusRepository.setCommentsAsReadByAccount(comments, account);
    }
	
	@Transactional
	public void setCommentsAsReadByAccount(List<Comment> comments, List<CommentReply> commentReplies) {
	    if ((comments == null || comments.isEmpty()) && (commentReplies == null || commentReplies.isEmpty())) {
	        throw new IllegalArgumentException("Danh sách comment hoặc comment reply không được đồng thời rỗng.");
	    }

	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    Account account = accountRepository.findByUsernameAndDeletedDateIsNull(username)
	            .orElseThrow(() -> new RuntimeException("Account not found"));

	    // Đánh dấu comment là đã đọc
	    if (comments != null && !comments.isEmpty()) {
	        commentReadStatusRepository.markCommentsAsReadByAccount(comments, account, CommentType.COMMENT);
	    }

	    // Đánh dấu comment reply là đã đọc
	    if (commentReplies != null && !commentReplies.isEmpty()) {
	        commentReadStatusRepository.markRepliesAsReadByAccount(commentReplies, account, CommentType.REPLY);
	    }
	}

}
