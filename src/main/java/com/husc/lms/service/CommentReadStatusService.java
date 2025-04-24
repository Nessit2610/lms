package com.husc.lms.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Comment;
import com.husc.lms.repository.AccountRepository;
import com.husc.lms.repository.CommentReadStatusRepository;

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

}
