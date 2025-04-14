package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentLessonChapterProgress;

@Repository
public interface StudentLessonChapterProgressRepository extends JpaRepository<StudentLessonChapterProgress, String> {

}
