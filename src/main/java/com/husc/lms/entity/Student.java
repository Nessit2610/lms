package com.husc.lms.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    private String typeOfResidence;

    @JoinColumn(name = "clazzId")
    @ManyToOne(fetch = FetchType.EAGER)
    private Class clazz;

    @JoinColumn(name = "majorId", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Major major; //Ngành học

    @OneToMany(mappedBy = "student")
    private List<Curriculum> curriculum;

    private String featured; //Trạng thái đặc biệt (có thể đánh dấu sinh viên nổi bật)

    private String decisionNumber; //Số quyết định nhập học

    private Date decisionDate; //Ngày quyết định nhập học

    @JoinColumn(name = "userId")
    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    private String avatar;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<LearningPlan> learningPlans;

    private String backgroundComposition;

    private String personalPriorityCategory;

    private String familyPriorityCategory;

    private String maritalStatus;

    private String height;

    private String weight;

    private Date unionJoiningDate;

    private String unionInductionPlace;

    private Date partyJoiningDate;

    private String officialPartyJoiningDate;

    private String partyInductionPlace;

    private String highSchoolGraduationYear;

    private String graduationClassification;

    private String graduationPlace;

    private String academicClassificationGrade10;

    private String academicClassificationGrade11;

    private String academicClassificationGrade12;

    private String conductClassificationGrade10;

    private String conductClassificationGrade11;

    private String conductClassificationGrade12;

    private Boolean hasParticipatedInArmedForces;

    private Date enlistmentDate;

    private Date dischargeDate;

    private String highestMilitaryRank;

    private String stationOrWorkplace;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private String deletedBy;
    
    private Date deletedDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "student")
    private LearningResult learningResult;
}
