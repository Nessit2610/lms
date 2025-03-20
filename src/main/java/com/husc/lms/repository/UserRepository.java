package com.husc.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{

	public boolean existsByUsername(String username);
	public Optional<User> findByUsername(String username); 
}
