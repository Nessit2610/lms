package com.husc.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Chapter;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

	List<Lesson> findByCourseAndDeletedDateIsNull(Course course);

	List<Lesson> findByCourseAndDeletedDateIsNullOrderByOrderAsc(Course course);
	
	Optional<Lesson> findByCourseAndOrderAndDeletedDateIsNull(Course course, Integer order);
	
	Lesson findByIdAndDeletedDateIsNull(String id);
	
	boolean existsByCourseAndOrderAndDeletedDateIsNull(Course course, Integer order);

	@Query("SELECT COUNT(l) FROM Lesson l WHERE l.course = :course AND l.deletedDate IS NULL")
	long countLessonsByCourse(@Param("course") Course course);

	@Query("SELECT l FROM Lesson l JOIN l.chapter c WHERE c = :chapter AND l.deletedDate IS NULL")
	Lesson findByChapterAndDeletedDateIsNull(@Param("chapter") Chapter chapter);
}
