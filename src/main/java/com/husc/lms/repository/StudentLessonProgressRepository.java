package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.StudentLessonProgress;

@Repository
public interface StudentLessonProgressRepository extends JpaRepository<StudentLessonProgress, String> {

	public StudentLessonProgress findByLesson(Lesson lesson);

	public StudentLessonProgress findByLessonAndStudent(Lesson lesson, Student student);
	
	@Query("SELECT slp.student.id FROM StudentLessonProgress slp WHERE slp.lesson.id = :lessonId AND slp.isCompleted = true")
	List<String> findStudentIdsByLessonCompleted(@Param("lessonId") String lessonId);

}
