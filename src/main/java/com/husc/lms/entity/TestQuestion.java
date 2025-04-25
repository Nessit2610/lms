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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lms_test_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestQuestion {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testId")
    private TestInGroup test;

    private String content; // Nội dung câu hỏi

    /**
     * Loại câu hỏi:
     * - "text": trả lời tự luận
     * - "single_choice": chọn một
     * - "multiple_choice": chọn nhiều
     */
    private String type;

    /**
     * Các lựa chọn trả lời, phân cách bằng JSON hoặc ký tự đặc biệt (nếu muốn đơn giản).
     * VD: ["A. Java", "B. Python", "C. C++"]
     */
    @Column(columnDefinition = "TEXT")
    private String options;

    /**
     * Đáp án đúng (nếu muốn lưu sẵn để chấm điểm tự động)
     * VD: "A", hoặc "A,B" nếu là multiple
     */
    private String correctAnswers;
}
