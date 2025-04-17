package com.husc.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Major;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {

	@Query("SELECT m FROM Major m WHERE m.deletedDate IS NULL")
	List<Major> findAllActiveMajors();

}
