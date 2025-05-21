package com.husc.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Account;
import com.husc.lms.entity.Teacher;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, String> {
	public Teacher findByAccountAndDeletedDateIsNull(Account account);
	@Query("SELECT t FROM Teacher t " +
	       "WHERE (:keyword IS NULL OR " +
	       "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	       "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<Teacher> searchTeacherByKeyword(
	    @Param("keyword") String keyword,
	    Pageable pageable
	);
}
