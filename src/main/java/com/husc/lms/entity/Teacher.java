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
@Table(name = "lms_teacher")
public class Teacher {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
	
    private String code;

    private String fullName;

    private String firstName;

    private String lastName;

    private String gender;

    private String email;

    private String idCardNumber;

    private String status;

    private String phoneNumber;

    private String contactAddress;

    private String majorId;

    private String userId;

    private byte[] avatar;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;

    private Date deletedDate;
    
    @Column(name="FOREIGN_LANGUAGE_PROFICIENCY")
    private String foreignLanguageProficiency;
    
    @Column(name="STATE_MANAGEMENT_LEVEL")
    private String stateManagementLevel;
    
    private String background;
    
    private Date dateOfBirth;
    
    private String educationEntryDate;
    
    private String educationalLevel;
    
    private String educationalManagementLevel;
    
    private String ethnicity;
    
    private String fieldOfWork;
    
    private Boolean getMarried;
    
    private String	itProficiencyLevel;
    
    private String nationality;
    
    private String organizationEntryDate;
    
    private Date partyEntryDate;
    
    private String permanentResidence;
    
    private String placeOfBirth;
    
    private String placeOfPartyAdmission;
    
    private String politicalTheoryLevel;
    
    private String professionalQualification;
    
    private String staffClassification;
    
    private Date unionEntryDate;
    

}
