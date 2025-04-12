package com.husc.lms.entity;

import java.util.Date;

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

    @JoinColumn(name = "senderAccountId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Account sender;
    
    @JoinColumn(name = "receiverAccountId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Account receiver;
    
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;

    private Date createdAt;
}
