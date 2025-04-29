package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentGroup;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup,String> {

}
