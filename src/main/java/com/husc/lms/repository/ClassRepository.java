package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {

}
