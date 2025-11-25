package com.server.sellernexus.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.server.sellernexus.exception.BadRequestException;
import com.server.sellernexus.exception.ResourceNotFoundException;
//import com.server.sellernexus.model.sales.Customer;
import com.server.sellernexus.model.users.Role;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.model.users.UserRole;
import com.server.sellernexus.repository.RoleRepository;
import com.server.sellernexus.repository.UserRoleRepository;
//import com.server.sellernexus.repository.customer.CustomerRepository;
import com.server.sellernexus.repository.user.UserRepository;
import com.server.sellernexus.security.UserPrincipal;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

//	@Autowired
//	private CustomerRepository customerRepo;

	public User create(User user) {
		Optional<Role> defaultRole = roleRepo.findById(Role.DEFAULT);
		user = userRepo.save(user);
		UserRole userRole = new UserRole();
		userRole.setRole(defaultRole.get());
		userRole.setUser(user);
		userRole = userRoleRepo.save(userRole);
		user.setUserRoles(Arrays.asList(userRole));
		return user;
	}

	public User update(User user) {
		User u = userRepo.findById(user.getId()).get();
		u.setName(user.getName());
		u.setImageUrl(user.getImageUrl());
		return userRepo.save(u);
	}

	@PreAuthorize("hasRole('" + Role.ADMIN + "')")
	public void addRole(Integer userId, String roleId) {
		if (userId == null || roleId == null)
			throw new BadRequestException("User and Role are required.");

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		Role role = roleRepo.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleId));

		UserRole exists = userRoleRepo.findByUserAndRole(user, role);
		if (exists != null) {
			// do nothing
			return;
		}

		UserRole newUserRole = new UserRole();
		newUserRole.setUser(user);
		newUserRole.setRole(role);
		userRoleRepo.save(newUserRole);
	}

	@PreAuthorize("hasRole('" + Role.ADMIN + "')")
	public void removeRole(Integer userId, String roleId) {

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		Role role = roleRepo.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleId));

		UserRole exists = userRoleRepo.findByUserAndRole(user, role);
		if (exists == null) {
			// do nothing
			return;
		}

		userRoleRepo.delete(exists);
	}

	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		return userPrincipal.getRef();
	}

	public Integer getOrganizationId() {
        User currentUser = getCurrentUser();
        // Assuming the organization ID is a field in the User entity, replace this with your actual field name
        return currentUser.getOrganizationId();
    }

    public Integer getPositionId() {
        User currentUser = getCurrentUser();
        // Assuming the position ID is a field in the User entity, replace this with your actual field name
        return currentUser.getPositionId();
    }

//	public void delete(Integer id) {
//		User x = userRepo.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
//
//		List<Customer> customers = customerRepo.findByCreatedBy(x);
//
//		if (!customers.isEmpty())
//			throw new BadRequestException("This User has " + customers.size() + " customers and cannot be deleted");
//
//		x.getUserRoles().forEach(ur -> {
//			userRoleRepo.delete(ur);
//		});
//
//		userRepo.delete(x);
//	}

}
