package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

}
