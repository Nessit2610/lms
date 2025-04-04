package com.husc.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

	public boolean existsByUsername(String username);
	public Optional<Account> findByUsername(String username); 
}
