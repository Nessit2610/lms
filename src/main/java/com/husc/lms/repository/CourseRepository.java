package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

}
