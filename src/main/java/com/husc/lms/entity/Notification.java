package com.husc.lms.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "lms_notification")
public class Notification {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	private String title;
	
	private String description;
	
	private String detail;
	
	private String type;
	
	private String createdBy;
	
	private Date createdDate;
	
	private String lastModifiedBy;
	
	private Date lastModifiedDate;
	
	private String deletedBy;
	
	private Date deletedDate;
	
	
	
	
	
	
	
	
	
	
	
}
