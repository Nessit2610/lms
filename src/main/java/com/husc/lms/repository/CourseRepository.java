package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.Teacher;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

	List<Course> findByStatusAndDeletedDateIsNull(String status);
	
	List<Course> findByTeacherAndDeletedDateIsNull(Teacher teacher);
	
	Course findByIdAndDeletedDateIsNull(String id);
}
