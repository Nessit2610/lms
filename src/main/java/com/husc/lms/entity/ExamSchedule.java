package com.husc.lms.entity;

import java.util.Date;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="lms_exam_schedule")
public class ExamSchedule {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examSectionId")
    private ExamSection examSection;

    private Date startDate;

    private Date endDate;

    private Integer timeDuration;

    @OneToMany(mappedBy = "examSchedule", cascade = CascadeType.ALL)
    private List<LearningPlan> learningPlans;

    @OneToMany(mappedBy = "examSchedule", cascade = CascadeType.ALL)
    private List<ExamScheduleRoom> examScheduleRooms;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
