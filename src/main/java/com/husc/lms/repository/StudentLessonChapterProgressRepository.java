package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentLessonChapterProgress;
import com.husc.lms.entity.Chapter;
import java.util.List;
import com.husc.lms.entity.Student;


@Repository
public interface StudentLessonChapterProgressRepository extends JpaRepository<StudentLessonChapterProgress, String> {

	public StudentLessonChapterProgress findByStudentAndChapter(Student student, Chapter chapter);
    boolean existsByStudentAndChapter(Student student, Chapter chapter);  // Cập nhật để dùng accountId

}
