package com.husc.lms.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name="lms_progress_score")
public class ProgressScore {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learningPlanId")
    private LearningPlan learningPlan;

    private Double attendanceScore;

    private Double testScore1;

    private Double testScore2;

    private Double testScore3;

    private Double finalScore;

    private Integer version;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
