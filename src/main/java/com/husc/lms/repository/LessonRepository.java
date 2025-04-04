package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

}
