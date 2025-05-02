package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestInGroup;

@Repository
public interface TestInGroupRepository extends JpaRepository<TestInGroup, String> {

}
