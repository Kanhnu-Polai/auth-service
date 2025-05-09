package com.skillverify.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillverify.authservice.dto.AuthResponseDto;
import com.skillverify.authservice.dto.LoginRequestDto;
import com.skillverify.authservice.dto.SignUpRequestDto;
import com.skillverify.authservice.entity.User;
import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;
import com.skillverify.authservice.exception.UserAlreadyExistsException;
import com.skillverify.authservice.httpengine.NotificationEngine;
import com.skillverify.authservice.repository.UserRepository;
import com.skillverify.authservice.security.jwtutils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
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
	
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
		
		if(userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
			throw new UserAlreadyExistsException(ErrorCodeEnum.USER_ALREADY_EXISTS);
		}
		// Create a new user entity
		User user = User.builder()
				.name(signUpRequestDto.getName())
				.email(signUpRequestDto.getEmail())
				.password(passwordEncoder.encode(signUpRequestDto.getPassword()))
				.role(signUpRequestDto.getRole())
				.build();
		
		userRepository.save(user);
		
		try {
			notificationEngine.makeCallToNotificationService(user.getEmail(), "Welcome To SkillVerify",
					"Welcome to SkillVerify, " + user.getName() + ". Your account has been created successfully.");
			
		} catch (Exception e) {
			log.error("Failed to send email notification: {}", e.getMessage());
		}
		return ResponseEntity.ok("User registered successfully");
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
	    // 1. Authenticate user credentials using Spring Security
	    Authentication authentication = authenticationManager.authenticate(
	        new UsernamePasswordAuthenticationToken(
	            loginRequestDto.getEmail(), 
	            loginRequestDto.getPassword()
	        )
	    );

	    // 2. If credentials are valid, get user details
	    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

	    // 3. Generate a JWT token
	    String jwtToken = jwtUtils.generateToken(userDetails);

	    // 4. Return the token in response
	    return ResponseEntity.ok(new AuthResponseDto(jwtToken, userDetails.getUsername(), userDetails.getAuthorities().toString()));
	}
	
	
	
	
	
	
	
	

}
