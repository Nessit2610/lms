package com.husc.lms.entity;

import java.time.OffsetDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.husc.lms.enums.NotificationType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lms_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiveAccountId")
    @JsonBackReference
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    @JsonBackReference
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentReplyId")
    @JsonBackReference
    private CommentReply commentReply;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joinClassRequestId")
    @JsonBackReference
    private JoinClassRequest joinClassRequest;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "messageId")
//    @JsonBackReference
//    private Message message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    @JsonBackReference
    private Post post;

    private String chatMessageId;
    
    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
    private String type;

    private boolean isRead;

    @Column(columnDefinition = "TEXT")
    private String description;

    private OffsetDateTime createdAt;
}
