package com.skillverify.authservice.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.skillverify.authservice.dto.AuthResponseDto;
import com.skillverify.authservice.dto.LoginRequestDto;
import com.skillverify.authservice.dto.SignUpRequestDto;
import com.skillverify.authservice.entity.User;
import com.skillverify.authservice.exception.TokenExpireException;
import com.skillverify.authservice.exception.TokenNullException;
import com.skillverify.authservice.exception.UserAlreadyExistsException;
import com.skillverify.authservice.httpengine.NotificationEngine;
import com.skillverify.authservice.httpengine.UserServiceEngine;
import com.skillverify.authservice.repository.UserRepository;
import com.skillverify.authservice.security.jwtutils.JwtUtils;
import com.skillverify.authservice.utils.Role;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private NotificationEngine notificationEngine;

	@Autowired
	private UserServiceEngine userServiceEngine;

	private final String className = this.getClass().getSimpleName();

//	@org.springframework.transaction.annotation.Transactional
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
		String methodName = "signUp";
		log.info("{} || {}() : Received signup request for email: {}", className, methodName,
				signUpRequestDto.getEmail());

		// Validate request
		if (signUpRequestDto.getEmail() == null || signUpRequestDto.getPassword() == null) {
			log.warn("{} || {}() : Email or password is null in signup request", className, methodName);
			return ResponseEntity.badRequest().body("Email and password must not be null");
		}

		// 1. Check in database if user exist
		if (userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
			log.warn("{} || {}() : Email already exists: {}", className, methodName, signUpRequestDto.getEmail());
			throw new UserAlreadyExistsException();
		}
		log.info("{} || {}() : Email not found, proceeding with signup for: {}", className, methodName,
				signUpRequestDto.getEmail());

		try {
			User user = User.builder().name(signUpRequestDto.getName()).email(signUpRequestDto.getEmail())
					.password(passwordEncoder.encode(signUpRequestDto.getPassword())).role(signUpRequestDto.getRole())
					.build();

			// 2.Save in database auth-datababe
			userRepository.save(user);
			log.info("{} || {}() : User saved successfully: {}", className, methodName, user.getEmail());

			// 3.Generate internal token for the user

			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					signUpRequestDto.getEmail(), signUpRequestDto.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String internalToken = jwtUtils.generateToken(userDetails, signUpRequestDto.getRole());

			boolean success = saveUser(internalToken, signUpRequestDto.getEmail(),
					signUpRequestDto.getRole().toString());

			if (!success) {
				// delete user from auth-db
				// return ResponseEnttity as internal server error
				log.error("{} || {}() : Failed to save user in user-service for email: {}", className, methodName,
						signUpRequestDto.getEmail());
				userRepository.deleteByEmail(signUpRequestDto.getEmail());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Failed to register user in user-service");

			}

			notificationEngine.makeCallToNotificationService(user.getEmail(), "Welcome To SkillVerify",
					"Welcome to SkillVerify, " + user.getName() + ". Your account has been created successfully.");

			log.info("{} || {}() : Notification sent successfully to {}", className, methodName, user.getEmail());
		} catch (Exception e) {
			log.error("{} || {}() : Exception occurred while signup or notification: {}", className, methodName,
					e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong during registration");
		}

		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
		String methodName = "login";
		log.info("{} || {}() : Login request received for email: {}", className, methodName,
				loginRequestDto.getEmail());

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwtToken = jwtUtils.generateToken(userDetails, getRoleFromEmail(loginRequestDto.getEmail()));

			log.info("{} || {}() : JWT Token generated for user: {}", className, methodName, userDetails.getUsername());

			return ResponseEntity.ok(
					new AuthResponseDto(jwtToken, userDetails.getUsername(), userDetails.getAuthorities().toString()));

		} catch (Exception e) {
			log.error("{} || {}() : Invalid login attempt for email: {}, Error: {}", className, methodName,
					loginRequestDto.getEmail(), e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
		String methodName = "validateToken";
		log.info("{} || {}() : Token validation request received", className, methodName);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("{} || {}() : Missing or malformed Authorization header", className, methodName);
			throw new TokenExpireException();
		}

		try {
			String token = authHeader.substring(7);
			String email = jwtUtils.extractUsername(token);

			if (email == null || jwtUtils.isTokenExpired(token)) {
				log.warn("{} || {}() : Token is invalid or expired", className, methodName);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token"));
			}

			Optional<User> optionalUser = userRepository.findByEmail(email);
			if (optionalUser.isEmpty()) {
				log.warn("{} || {}() : User not found for email: {}", className, methodName, email);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
			}

			User user = optionalUser.get();
			Map<String, Object> response = new HashMap<>();
			response.put("email", user.getEmail());
			response.put("role", user.getRole());
			response.put("status", "valid");

			log.info("{} || {}() : Token validated for user: {}", className, methodName, user.getEmail());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			log.error("{} || {}() : Error during token validation: {}", className, methodName, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Token validation failed"));
		}
	}
	
	
	
	
	

	private Role getRoleFromEmail(String email) {
		return userRepository.findByEmail(email).map(User::getRole).orElse(null);
	}

	private boolean saveUser(String token, String email, String role) {
		if (token == null) {
			throw new TokenNullException();
		}

		return userServiceEngine.callUserService(email, token, role);

	}
	
	
	@GetMapping("/health")
	public String health() {
	    return "AuthService is running!";
	}

}