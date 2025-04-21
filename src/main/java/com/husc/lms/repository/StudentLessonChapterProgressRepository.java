package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentLessonChapterProgress;
import com.husc.lms.entity.Chapter;
import java.util.List;
import com.husc.lms.entity.Student;


@Repository
public interface StudentLessonChapterProgressRepository extends JpaRepository<StudentLessonChapterProgress, String> {

	public StudentLessonChapterProgress findByStudentAndChapter(Student student, Chapter chapter);
    boolean existsByStudentAndChapter(Student student, Chapter chapter);  // Cập nhật để dùng accountId
    @Query("SELECT scp.student.id FROM StudentLessonChapterProgress scp WHERE scp.chapter.id = :chapterId AND scp.isCompleted = true")
    List<String> findStudentIdsByChapterCompleted(@Param("chapterId") String chapterId);

}
