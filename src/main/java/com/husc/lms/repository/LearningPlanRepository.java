package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.LearningPlan;

@Repository
public interface LearningPlanRepository extends JpaRepository<LearningPlan,String> {

}
