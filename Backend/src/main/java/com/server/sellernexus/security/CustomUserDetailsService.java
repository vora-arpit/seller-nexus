package com.server.sellernexus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.sellernexus.exception.ResourceNotFoundException;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.repository.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

		return UserPrincipal.create(user);
	}

	@Transactional
	public UserDetails loadUserById(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException("User", "id", userId));

		return UserPrincipal.create(user);
	}
}