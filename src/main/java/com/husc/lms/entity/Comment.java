package com.husc.lms.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
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
@Table(name = "lms_comment")
public class Comment {
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @JoinColumn(name = "accountId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Account account;

    @JoinColumn(name = "lessonId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Lesson lesson;

    @JoinColumn(name = "courseId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Course course;
    
    private String detail;

    @Column(name = "createdDate")
    private OffsetDateTime createdDate;

    @Column(name = "deletedBy")
    private String deletedBy;

    @Column(name = "deletedDate")
    private OffsetDateTime deletedDate;
}
