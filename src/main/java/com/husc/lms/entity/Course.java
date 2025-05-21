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
@Table(name = "lms_course")
public class Course {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
    private String name;

    private String description;

    private Date startDate;

    private Date endDate;

    private String major;

    private String status;
    
    private String image;
    
    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    private List<Lesson> lesson;
    
    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    private List<StudentCourse> studentCourses;
    
    private String learningDurationType;
    
    @JoinColumn(name = "teacherId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Teacher teacher;
    
    private String feeType;
    
    private Double price;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
