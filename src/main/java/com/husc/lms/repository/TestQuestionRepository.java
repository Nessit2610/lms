package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.TestQuestion;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, String> {

	List<TestQuestion> findByTestInGroup(TestInGroup testInGroup);
	

}
