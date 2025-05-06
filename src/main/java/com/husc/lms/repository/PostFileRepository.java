package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.PostFile;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, String> {

}
