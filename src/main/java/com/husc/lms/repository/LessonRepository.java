package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Course;


@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {
	List<Lesson> findByCourseAndDeletedDateIsNull(Course course);
	Lesson findByIdAndDeletedDateIsNull(String id);
}
