package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.TestInGroup;
import java.util.List;
import com.husc.lms.entity.Group;


@Repository
public interface TestInGroupRepository extends JpaRepository<TestInGroup, String> {
	List<TestInGroup> findByGroup(Group group);

}
