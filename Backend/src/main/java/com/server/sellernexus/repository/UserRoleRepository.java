package com.server.sellernexus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.sellernexus.model.users.Role;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.model.users.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	public UserRole findByUserAndRole(User user, Role role);
}
