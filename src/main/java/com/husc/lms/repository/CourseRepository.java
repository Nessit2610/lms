package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

	List<Course> findByStatus(String status);
}
