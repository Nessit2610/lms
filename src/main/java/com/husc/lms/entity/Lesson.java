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
@Table(name = "lms_lesson")
public class Lesson {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@JoinColumn(name = "courseId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Course course;
 
    private String description;
    
    @OneToMany(mappedBy = "lesson")
    private List<LessonMaterial> lessonMaterial;
    
    @OneToMany(mappedBy = "lesson")
    private List<LessonQuiz> lessonQuiz;
 
    @Column(name = "`order`")
    private Integer order;
 
    private String createdBy;
 
    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
}
