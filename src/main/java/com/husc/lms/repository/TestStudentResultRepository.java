package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestStudentResult;
import com.husc.lms.entity.TestInGroup;
import java.util.List;
import com.husc.lms.entity.Student;


@Repository
public interface TestStudentResultRepository extends JpaRepository<TestStudentResult, String> {
	TestStudentResult findByStudentAndTestInGroup(Student student, TestInGroup testInGroup);
}
