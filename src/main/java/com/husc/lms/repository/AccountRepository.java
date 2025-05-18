package com.husc.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

	public boolean existsByUsername(String username);

	public Optional<Account> findByUsernameAndDeletedDateIsNull(String username);

	@Query("SELECT r.name FROM Account a JOIN a.roles r WHERE a.id = :id")
	List<String> findRoleNamesByAccountId(@Param("id") String accountId);

//	@Query("SELECT a FROM Account a WHERE a.deletedDate IS NULL AND (LOWER(a.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(a.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND a.username NOT IN :excludedUsernames")
//	List<Account> findBySearchTermAndUsernameNotInAndDeletedDateIsNull(
//			@Param("searchTerm") String searchTerm,
//			@Param("excludedUsernames") List<String> excludedUsernames);
}
