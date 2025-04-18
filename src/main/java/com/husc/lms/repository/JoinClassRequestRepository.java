package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.JoinClassRequest;
import com.husc.lms.entity.Course;
import java.util.List;
import com.husc.lms.entity.Student;


@Repository
public interface JoinClassRequestRepository extends JpaRepository<JoinClassRequest, String> {

	JoinClassRequest findByStudentAndCourse(Student student, Course course);

	@Query("SELECT j.student FROM JoinClassRequest j WHERE j.course.id = :courseId AND j.status = :status")
	List<Student> findAllStudentsByCourseIdAndStatus(@Param("courseId") String courseId, @Param("status") String status);


    
	@Query("SELECT j.course FROM JoinClassRequest j WHERE j.student.id = :studentId AND j.course.deletedDate IS NULL AND j.status = :status")
	List<Course> findAllCoursesByStudentIdAndStatus(@Param("studentId") String studentId, @Param("status") String status);


	boolean existsByStudentAndCourseAndStatus(Student student, Course course, String status);


}
