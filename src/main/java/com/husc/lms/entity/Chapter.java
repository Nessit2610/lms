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

@Entity
@Table(name = "lms_chapter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	private String path;
 
    private String type;
    
    private Integer order;
     
    @JoinColumn(name = "lessonId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;
    
    private String createdBy;
     
    private Date createdDate;
    
    private String lastModifiedBy;
    
    private Date lastModifiedDate;
     
    private String deletedBy;
    
    private Date deletedDate;
}
