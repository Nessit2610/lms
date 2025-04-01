//package com.husc.lms.entity;
//
//import java.util.Date;
//import java.util.List;
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name="lms_final_score")
//public class FinalScore {
//
//	@Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(length = 36)
//    private String id;
//	
//	@OneToMany(mappedBy = "finalScore", cascade = CascadeType.ALL)
//    private List<TotalScore> totalScores; // Danh sách điểm của tất cả môn học
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "studentId")
//    private Student student; // Liên kết với sinh viên
//
//    private Double averageScore; // Điểm trung bình chung của sinh viên
//
//    private String createdBy;
//
//    private Date createdDate;
//
//    private String lastModifiedBy;
//
//    private Date lastModifiedDate;
//
//    private String deletedBy;
//
//    private Date deletedDate;
//    
//    @JoinColumn(name = "LEARNING_RESULT_ID")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private LearningResult learningResult;
//}
