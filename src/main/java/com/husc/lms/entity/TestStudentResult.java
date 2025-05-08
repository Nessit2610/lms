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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lms_test_student_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestStudentResult {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testId")
    private TestInGroup testInGroup;

    private int totalCorrect; // Số câu đúng

    private double score; // Điểm

    private Date startedAt;

    private Date submittedAt;

    @OneToMany(mappedBy = "testStudentResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestStudentAnswer> testStudentAnswer;

}
