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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="lms_exam_schedule_room")
public class ExamScheduleRoom {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examScheduleId", nullable = false)
    private ExamSchedule examSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", nullable = false)
    private Room room;

    @Column(name = "numberOfStudents")
    private Integer numberOfStudents;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
	
}
