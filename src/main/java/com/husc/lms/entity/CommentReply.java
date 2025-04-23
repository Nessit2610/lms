package com.husc.lms.entity;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lms_comment_reply")
public class CommentReply {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ownerAccountId")
    @JsonBackReference
    private Account ownerAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "replyAccountId")
    @JsonBackReference
    private Account replyAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chapterId")
    @JsonBackReference
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "courseId")
    @JsonBackReference
    private Course course;

    private String detail;

    @Column(name = "createdDate")
    private OffsetDateTime createdDate;

    @Column(name = "deletedBy")
    private String deletedBy;

    @Column(name = "deletedDate")
    private OffsetDateTime deletedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    @JsonBackReference
    private Comment comment;

    @OneToMany(mappedBy = "commentReply")
    @JsonManagedReference
    private List<Notification> notifications;
}
