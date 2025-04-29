package com.husc.lms.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "lms_student")
public class Student {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String fullName;
    
    private String email;
    
    private String description;
    
    @JoinColumn(name = "majorId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Major major;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accountId")
    private Account account;
    
    private String avatar;

    @OneToMany(mappedBy = "student")
    private List<StudentGroup> studentGroups;
}
