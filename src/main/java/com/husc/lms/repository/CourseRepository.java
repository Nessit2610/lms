package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.Teacher;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {

	Page<Course> findByStatus(String status, Pageable pageable);
	
	Page<Course> findByStatusInAndDeletedDateIsNull(List<String> statuses, Pageable pageable);
	
	Page<Course> findByTeacherAndDeletedDateIsNull(Teacher teacher, Pageable pageable);
	
	Course findByIdAndDeletedDateIsNull(String id);
	Course findByIdAndTeacherAndDeletedDateIsNull(String id, Teacher teacher);
	
	@Query("SELECT c FROM Course c " +
	       "WHERE (c.status = 'PUBLIC' OR c.status = 'REQUEST') " +
	       "AND c.deletedDate IS NULL " +
	       "AND (:courseName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :courseName, '%'))) " +
	       "AND (:teacherName IS NULL OR LOWER(c.teacher.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%')))")
	Page<Course> searchByCourseNameAndTeacherName(
	    @Param("courseName") String courseName,
	    @Param("teacherName") String teacherName,
	    Pageable pageable
	);


	@Query("SELECT c FROM Course c ORDER BY CASE WHEN c.major = :major THEN 0 ELSE 1 END")
	Page<Course> findAllOrderByMatchingMajorFirst(@Param("major") String major, Pageable pageable);

}
