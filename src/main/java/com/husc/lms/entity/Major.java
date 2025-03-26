package com.husc.lms.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="lms_major")
public class Major {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
	private String code;
	
	private String moetCode;
	
	private int status;
	
	private String name;
	
	private String faculty;
	
	private String createdBy;
	
	private Date createdDate;
	
	private String lastModifiedBy;
	
	private Date lastModifiedDate;
	
	private String deletedBy;
	
	private String deletedDate;
	
	
}
