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
	
<<<<<<< HEAD
	@Query("SELECT c FROM Course c " +
	       "WHERE (:courseName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :courseName, '%'))) " +
	       "AND (:teacherName IS NULL OR LOWER(c.teacher.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%')))")
	List<Course> searchByCourseNameAndTeacherName(
	    @Param("courseName") String courseName,
	    @Param("teacherName") String teacherName
	);

=======
>>>>>>> 77f572f66344fbf801892edbd0e389f1ff3072f2

}
