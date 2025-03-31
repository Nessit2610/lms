package com.husc.lms.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "lms_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String code;
    private String fullName;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String placeOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String contactAddress;
    private String permanentResidence; //Địa chỉ thường trú
    
    @JoinColumn(name = "clazzId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Class classId;
    
    @JoinColumn(name = "majorId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Major majorId; //Ngành học
  
    
    private String featured; //Trạng thái đặc biệt (có thể đánh dấu sinh viên nổi bật)
    private String decisionNumber; //Số quyết định nhập học
    private Date decisionDate; //Ngày quyết định nhập học
    private String userId;
    
    private String avatar;
      
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private String deletedBy;
    private Date deletedDate;
}
