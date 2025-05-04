package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Course;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentCourse;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, String> {
	@Query("SELECT sc.course FROM StudentCourse sc " +
	       "WHERE sc.student = :student AND sc.deletedDate IS NULL")
	Page<Course> findCourseByStudent(@Param("student") Student student, Pageable pageable);

	@Query("SELECT sc.student FROM StudentCourse sc " +
	       "WHERE sc.course = :course AND sc.deletedDate IS NULL")
	Page<Student> findStudentByCourse(@Param("course") Course course, Pageable pageable);

	@Query("SELECT COUNT(sc.student) FROM StudentCourse sc " +
	       "WHERE sc.course = :course AND sc.deletedDate IS NULL")
	long countStudentsByCourse(@Param("course") Course course);
	
	StudentCourse findByCourseAndStudentAndDeletedDateIsNull(Course course, Student student);
    
    List<StudentCourse> findByCourseAndDeletedDateIsNull(Course course);
    
    @Query("""
	    SELECT sc.student FROM StudentCourse sc
	    WHERE sc.course = :course
	    AND sc.deletedDate IS NULL
	    AND (
	        LOWER(sc.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	        OR LOWER(sc.student.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    )
	""")
	Page<Student> searchStudentsInCourse(@Param("course") Course course,
	                                     @Param("keyword") String keyword,
	                                     Pageable pageable);

    boolean existsByStudentAndCourse(Student student, Course course);

}