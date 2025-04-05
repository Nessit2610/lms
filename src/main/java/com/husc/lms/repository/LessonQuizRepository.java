package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.LessonQuiz;

@Repository
public interface LessonQuizRepository extends JpaRepository<LessonQuiz, String> {

}
