package com.husc.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.Permission;



@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{

}
