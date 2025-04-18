package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Lesson;
import com.husc.lms.entity.LessonMaterial;

@Repository
public interface LessonMaterialRepository extends JpaRepository<LessonMaterial, String> {

	List<LessonMaterial> findByLessonAndDeletedDateIsNull(Lesson lesson);
	LessonMaterial findByIdAndDeletedDateIsNull(String id);
}
