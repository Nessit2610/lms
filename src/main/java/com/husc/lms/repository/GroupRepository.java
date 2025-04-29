package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Group;
import com.husc.lms.entity.Teacher;
import java.util.List;


@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

	Group findByIdAndTeacher(String id, Teacher teacher);
}
