package com.husc.lms.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.husc.lms.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lms_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sendAccountId")
    @JsonBackReference
    private Account sendAccount;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiveAccountId")
    @JsonBackReference
    private Account receiveAccount;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private Date createAt;
    
    @OneToMany(mappedBy = "message")
    @JsonManagedReference // Đánh dấu đây là đối tượng "cha", mối quan hệ sẽ được serialize
    private List<Notification> notifications;
}
