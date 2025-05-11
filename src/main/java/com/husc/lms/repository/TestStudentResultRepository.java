package com.husc.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.entity.TestInGroup;
import java.util.Optional;

import com.husc.lms.entity.Student;


@Repository
public interface TestStudentResultRepository extends JpaRepository<TestStudentResult, String> {
	Optional<TestStudentResult> findByStudentAndTestInGroup(Student student, TestInGroup testInGroup);

	Page<TestStudentResult> findByTestInGroup(TestInGroup testInGroup, Pageable pageable);
}
