package com.husc.lms.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestInGroup;
import com.husc.lms.entity.Group;


@Repository
public interface TestInGroupRepository extends JpaRepository<TestInGroup, String> {
	Page<TestInGroup> findByGroup(Group group, Pageable pageable);
	
	
}
