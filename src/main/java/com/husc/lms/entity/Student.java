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
    private String clazzId;
    private String majorId; //Ngành học
    private String featured; //Trạng thái đặc biệt (có thể đánh dấu sinh viên nổi bật)
    private String decisionNumber; //Số quyết định nhập học
    private Date decisionDate; //Ngày quyết định nhập học
    private String userId;
    private byte[] avatar;
    private String createdBy;
    private Date createdDate;
    private String lastModifiedBy;
    private Date lastModifiedDate;
    private String deletedBy;
    private Date deletedDate;
}
