package com.husc.lms.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lms_comment_read_status")
public class CommentReadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @JoinColumn(name = "commentId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Comment comment;

    @JoinColumn(name = "accountId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Account account;

    @Column(name = "isRead")
    private Boolean isRead = false;
}
