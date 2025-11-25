package com.server.crm1.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.crm1.model.users.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {

	Optional<User> findByEmail(String email);

	

	Boolean existsByEmail(String email);

	

}
