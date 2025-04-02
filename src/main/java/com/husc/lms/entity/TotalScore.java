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
import jakarta.persistence.ManyToOne;
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
@Table(name="lms_total_score")
public class TotalScore {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "progressScoreId", nullable = false)
    private ProgressScore progressScore;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examScoreId", nullable = false)
    private ExamScore examScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "final_score_id")
    private FinalScore finalScore;

    private Double totalScore; // Tổng điểm

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
    
    @JoinColumn(name = "learningResultId")
    @ManyToOne(fetch = FetchType.LAZY)
    private LearningResult learningResult;
}
