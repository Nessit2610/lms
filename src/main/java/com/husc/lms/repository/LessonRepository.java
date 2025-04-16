package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Course;


@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
	List<Lesson> findByCourseAndDeletedDateIsNull(Course course);
	Lesson findByIdAndDeletedDateIsNull(String id);
	
	@Query("SELECT COUNT(l) FROM Lesson l WHERE l.course = :course")
    long countLessonsByCourse(@Param("course") Course course);
}
