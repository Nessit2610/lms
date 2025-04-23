package com.husc.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Chapter;
import com.husc.lms.entity.Lesson;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {

	Chapter findByIdAndDeletedDateIsNull(String id);
	List<Chapter> findByLessonAndDeletedDateIsNull(Lesson lesson);
	
	Optional<Chapter> findByLessonAndOrderAndDeletedDateIsNull(Lesson lesson, Integer order);
	boolean existsByLessonAndOrderAndDeletedDateIsNull(Lesson lesson, Integer order);

}
