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
@Table(name = "lms_chapter")
public class Chapter {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	private String name;
	
	private String path;
 
    private String type;
    
    @Column(name = "`order`")
    private Integer order;
     
    @JoinColumn(name = "lessonId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Lesson lesson;
    
    @OneToMany(mappedBy = "chapter")
    private List<Comment> comment;
    
    private String createdBy;
     
    private Date createdDate;
    
    private String lastModifiedBy;
    
    private Date lastModifiedDate;
     
    private String deletedBy;
    
    private Date deletedDate;
}
