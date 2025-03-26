package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Major;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {

}
