package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestStudentAnswer;

@Repository
public interface TestStudentAnswerRepository extends JpaRepository<TestStudentAnswer, String> {

}
