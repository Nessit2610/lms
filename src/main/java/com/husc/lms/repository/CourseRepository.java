package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Teacher;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

	List<Course> findByStatus(String status);
	
	List<Course> findByStatusInAndDeletedDateIsNull(List<String> statuses);
	
	List<Course> findByTeacherAndDeletedDateIsNull(Teacher teacher);
	
	Course findByIdAndDeletedDateIsNull(String id);
	
	@Query("SELECT c FROM Course c JOIN c.studentCourses sc WHERE sc.student = :student")
	List<Course> findByStudent(@Param("student") Student student);

}
