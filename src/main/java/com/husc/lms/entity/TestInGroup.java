package com.husc.lms.entity;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "lms_test_in_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestInGroup {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId")
    private Group group;

    private OffsetDateTime createdAt;

    private OffsetDateTime startedAt;
    
    private OffsetDateTime expiredAt;

    @OneToMany(mappedBy = "testInGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestQuestion> questions;
}
