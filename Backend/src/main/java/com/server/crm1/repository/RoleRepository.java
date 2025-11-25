package com.server.crm1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.crm1.model.users.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

	List<Role> findAllByOrderByNameDesc();

}
