package com.husc.lms.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.husc.lms.entity.Student;
import com.husc.lms.entity.Account;
import com.husc.lms.entity.Course;
import com.husc.lms.entity.Group;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

	public Student findByAccount(Account account);

	
	@Query("SELECT s FROM Student s " +
	           "WHERE (:fullName IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
	           "AND (:email IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
	           "AND (:majorName IS NULL OR LOWER(s.major.name) LIKE LOWER(CONCAT('%', :majorName, '%')))")
    Page<Student> searchStudent(
        @Param("fullName") String fullName,
        @Param("email") String email,
        @Param("majorName") String majorName,
        Pageable pageable
    );
	
	
	@Query("""
	    SELECT s FROM Student s
	    WHERE s NOT IN (
	        SELECT sg.student FROM StudentGroup sg
	        WHERE sg.group = :group
	    )
	    AND (
	        LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	        OR
	        LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    )
	""")
	Page<Student> findStudentsNotInGroup(@Param("group") Group group,
	                                     @Param("keyword") String keyword,
	                                     Pageable pageable);

}
