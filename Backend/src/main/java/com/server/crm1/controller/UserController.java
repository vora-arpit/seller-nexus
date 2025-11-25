package com.server.crm1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.crm1.exception.ResourceNotFoundException;
import com.server.crm1.model.users.Role;
import com.server.crm1.model.users.User;
import com.server.crm1.payload.ApiResponse;
import com.server.crm1.repository.RoleRepository;
import com.server.crm1.repository.user.UserRepository;
import com.server.crm1.service.UserService;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('" + Role.ADMIN + "')")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private UserService userService;

	@GetMapping("/roles")
	public List<Role> getAllRoles() {
		return roleRepo.findAllByOrderByNameDesc();
	}

	@PostMapping("/{userId}/roles")
	public ApiResponse addRole(@PathVariable Integer userId, @RequestBody Role role) {
		userService.addRole(userId, role.name);
		return new ApiResponse(true, "Role Added Successfully!");
	}

	@DeleteMapping("/{userId}/roles")
	public ApiResponse removeRole(@PathVariable Integer userId, @RequestBody Role role) {
		userService.removeRole(userId, role.name);
		return new ApiResponse(true, "Role Removed Successfully!");
	}

	@GetMapping
	public List<User> search(@RequestParam("filter") String filter) {
		return userRepo.search(filter);
	}

	@GetMapping("/{id}")
	public User findById(@PathVariable Integer id) {
		return userRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@PutMapping("/{id}")
	public User update(@PathVariable(value = "id") Integer id, @RequestBody User user) {
		user.setId(id);
		return userService.update(user);
	}

}
