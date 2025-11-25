package com.server.crm1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.crm1.model.users.Role;
import com.server.crm1.model.users.User;
import com.server.crm1.model.users.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	public UserRole findByUserAndRole(User user, Role role);
}
