package com.husc.lms.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.StudentGroup;
import com.husc.lms.entity.Student;
import com.husc.lms.entity.Group;


@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup,String> {

	StudentGroup findByStudentAndGroup(Student student, Group group);
	
	Page<StudentGroup> findByStudent(Student student, Pageable pageable);

    Page<StudentGroup> findByGroup(Group group, Pageable pageable);
    
    @Query("""
	    SELECT sg FROM StudentGroup sg
	    WHERE sg.group = :group
	    AND (
	        (LOWER(sg.student.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
	        OR
	        (LOWER(sg.student.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
	    )
	""")
	Page<StudentGroup> searchByFullNameOrEmail(@Param("group") Group group,
	                                           @Param("keyword") String keyword,
	                                           Pageable pageable);

    @Query("SELECT sg.student.id FROM StudentGroup sg WHERE sg.group.id = :groupId")
    Set<String> findStudentIdsByGroupId(@Param("groupId") String groupId);


	boolean existsByStudentAndGroup(Student student, Group group);
}
