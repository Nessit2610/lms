package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, String> {

}
