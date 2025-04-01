package com.husc.lms.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name="lms_learning_result")
public class LearningResult {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@JoinColumn(name = "studentId")
    @OneToOne(fetch = FetchType.LAZY)
    private Student student;
    @OneToMany(mappedBy = "learningResult")
    private List<TotalScore> totalScore;

    private Integer earnedRequiredCredits;

    private Integer earnedElectiveCredits;

    private Integer earnedTotalCredits;

    private Double gpaInNumber;

    private String gpaInWord;

    private String ranking;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
