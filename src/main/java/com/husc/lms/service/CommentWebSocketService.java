package com.husc.lms.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.husc.lms.dto.request.CommentMessage;
import com.husc.lms.dto.request.CommentUpdateMessage;
import com.husc.lms.dto.response.APIResponse;
import com.husc.lms.dto.response.CommentMessageResponse;
import com.husc.lms.dto.response.CommentUpdateMessageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendCommentToTopics(CommentMessageResponse response) {
        // Gửi thông báo đến topic comments (cho getCommentByChapter)
        messagingTemplate.convertAndSend("/topic/comments",
                APIResponse.<CommentMessageResponse>builder()
                        .result(response)
                        .build());

        // Gửi thông báo đến topic my-comments (cho myComments)
        messagingTemplate.convertAndSend("/topic/my-comments",
                APIResponse.<CommentMessageResponse>builder()
                        .result(response)
                        .build());
    }

    public void sendCommentUpdateToTopics(CommentUpdateMessageResponse response) {
        // Gửi thông báo đến cả hai topic
        messagingTemplate.convertAndSend("/topic/comments",
                APIResponse.<CommentUpdateMessageResponse>builder()
                        .result(response)
                        .build());

        messagingTemplate.convertAndSend("/topic/my-comments",
                APIResponse.<CommentUpdateMessageResponse>builder()
                        .result(response)
                        .build());
    }

    public void sendCommentDeleteToTopics(Boolean response) {
        // Gửi thông báo đến cả hai topic
        messagingTemplate.convertAndSend("/topic/comments",
                APIResponse.<Boolean>builder()
                        .result(response)
                        .build());

        messagingTemplate.convertAndSend("/topic/my-comments",
                APIResponse.<Boolean>builder()
                        .result(response)
                        .build());
    }
}