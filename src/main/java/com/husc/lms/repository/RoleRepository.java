package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
