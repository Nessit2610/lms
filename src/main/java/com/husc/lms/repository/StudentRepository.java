package com.husc.lms.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.husc.lms.entity.Student;
import com.husc.lms.entity.Account;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

	public Student findByAccount(Account account);
}
