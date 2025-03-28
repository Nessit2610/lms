package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Class;
import com.husc.lms.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

	public Student findByUserId(String userId);
	public List<Student> findByClassId(Class classId);
}
