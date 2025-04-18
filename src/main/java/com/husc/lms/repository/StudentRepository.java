package com.husc.lms.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.husc.lms.entity.Student;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

	public Student findByAccount(Account account);
	
	@Query("SELECT s FROM Student s JOIN s.studentCourses sc WHERE sc.course = :course")
	List<Student> findByCourse(@Param("course") Course course);
	
	@Query("SELECT s FROM Student s WHERE s.id NOT IN (" +
		       "SELECT s2.id FROM Student s2 JOIN s2.studentCourses sc WHERE sc.course = :course)")
	List<Student> findStudentsNotInCourse(@Param("course") Course course);

}
