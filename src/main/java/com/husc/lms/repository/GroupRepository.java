package com.husc.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Group;
import com.husc.lms.entity.Teacher;


@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

	Group findByIdAndTeacher(String id, Teacher teacher);
	
	Page<Group> findByTeacher(Teacher teacher, Pageable pageable);
	
	Page<Group> findByTeacherAndNameContainingIgnoreCase(Teacher teacher, String keyword, Pageable pageable);

	
}
