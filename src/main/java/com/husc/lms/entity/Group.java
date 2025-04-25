package com.husc.lms.entity;

import java.time.OffsetDateTime;
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
@Table(name = "lms_group")
public class Group {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacherId")
	private Teacher teacher;
	
	@OneToMany(mappedBy = "group" ,fetch = FetchType.LAZY)
	private List<Post> post;
	
	@OneToMany(mappedBy = "group" ,fetch = FetchType.LAZY)
	private List<StudentGroup> studentGroups;
	
	private Date createdAt;
}
