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
@Table(name="lms_curriculum")
public class Curriculum {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
 	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "majorId")
    private Major major;


    private String name;

    private String type;
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL)
    private List<CurriculumSubject> curriculumSubjects;

    private Integer requiredCredits;

    private Integer electiveCredits;

    private Integer totalCredits;

    @JoinColumn(name = "studentId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
