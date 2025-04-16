package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.LessonQuiz;
import com.husc.lms.entity.Lesson;


@Repository
public interface LessonQuizRepository extends JpaRepository<LessonQuiz, String> {

	List<LessonQuiz> findByLessonAndDeletedDateIsNull(Lesson lesson);                                                                                                                                                                     
}
