package com.husc.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentGroup;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Group;


@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup,String> {

	StudentGroup findByStudentAndGroup(Student student, Group group);
	
	Page<StudentGroup> findByStudent(Student student, Pageable pageable);

    Page<StudentGroup> findByGroup(Group group, Pageable pageable);
	
}
