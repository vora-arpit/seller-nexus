package com.server.sellernexus.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.server.sellernexus.exception.BadRequestException;
import com.server.sellernexus.model.users.AuthProvider;
import com.server.sellernexus.model.users.Role;
import com.server.sellernexus.model.users.User;
import com.server.sellernexus.payload.ApiResponse;
import com.server.sellernexus.payload.AuthResponse;
import com.server.sellernexus.payload.ForgotRequest;
import com.server.sellernexus.payload.LoginRequest;
import com.server.sellernexus.payload.ResetPasswordRequest;
import com.server.sellernexus.payload.SignUpRequest;
import com.server.sellernexus.repository.user.UserRepository;
import com.server.sellernexus.security.TokenProvider;
import com.server.sellernexus.security.UserPrincipal;
import com.server.sellernexus.service.EmailService;
import com.server.sellernexus.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private EmailService emailService;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
	System.out.println("loginRequest"+loginRequest);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getEmail(),
						loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = tokenProvider.createToken(authentication);
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		
		User user = userRepository.findById(principal.getId()).get();
		AuthResponse res = new AuthResponse(token, user);
		return ResponseEntity.ok(res);
	}

	

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new BadRequestException("Email address already in use.");
		}

		// Creating user's account
		User user = new User();
		user.setName(signUpRequest.getName());
		user.setEmail(signUpRequest.getEmail());
		user.setPassword(signUpRequest.getPassword());
		user.setProvider(AuthProvider.local);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		User result = userService.create(user);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/user/me")
				.buildAndExpand(result.getId()).toUri();

		return ResponseEntity.created(location)
				.body(new ApiResponse(true, "User registered successfully!"));
	}

	@GetMapping("/current")
	@PreAuthorize("hasRole('" + Role.DEFAULT + "')")
	public User getCurrentUser() {
		return userService.getCurrentUser();
	}

	// @GetMapping("/forgot")
    // public String getUserIdByEmail(@RequestParam String email) {
    //     // Find the user by email
    //     Optional<User> userOptional = userRepository.findByEmail(email);
        
    //     // Check if the user exists
    //     if (userOptional.isPresent()) {
    //         // Retrieve the user from the optional
    //         User user = userOptional.get();
    //         // Retrieve the ID of the user
    //         Integer userId = user.getId();
    //         // Return the ID as a response
    //         return "User ID: " + userId;
    //     } else {
    //         // If user not found, return appropriate response
    //         return "User with email " + email + " not found";
    //     }
    // }


	@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@RequestBody ForgotRequest forgotPasswordRequest) {
    // Check if user exists with the provided email
    Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequest.getEmail());

    if (!userOptional.isPresent()) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, "User with this email does not exist."));
    }

    User user = userOptional.get();

    // Generate a time-bound security reset token
    String token = tokenProvider.createResetToken(new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), Collections.emptyList()));

    // Send the reset token in an email
    emailService.sendResetPasswordEmail(user.getEmail(), token);

    // Return success response
    return ResponseEntity.ok(new ApiResponse(true, "Reset password email sent successfully."));
}

	
	// @PutMapping("/reset/{userId}")
    // public ResponseEntity<?> setNewPassword(@PathVariable(value = "userId") Integer userId, @RequestBody String newPassword) {
    //     Optional<User> userOptional = userRepository.findById(userId);
        
    //     if (userOptional.isPresent()) {
    //         User user = userOptional.get();
    //         user.setPassword(passwordEncoder.encode(newPassword)); // Update password
    //         userRepository.save(user); // Save changes
    //         return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully."));
    //     } else {
    //         return ResponseEntity.badRequest().body(new ApiResponse(false, "User not found."));
    //     }
    // }

	@PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest  request) {
        String token = request.getToken();
        String password = request.getPassword();
		System.out.println("Token:-"+token);
		System.out.println("Password:-"+password);
        Integer UserId=tokenProvider.getUserIdFromToken(token);
        Optional<User> userOptional = userRepository.findById(UserId);
		User user = userOptional.get();
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully.");
    }
	

}
