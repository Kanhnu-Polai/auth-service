package com.skillverify.authservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillverify.authservice.dto.AdminAddReqDto;
import com.skillverify.authservice.dto.AdminResponseDto;
import com.skillverify.authservice.entity.User;
import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;
import com.skillverify.authservice.exception.UserAlreadyExistsException;
import com.skillverify.authservice.exception.UserNotFoundException;
import com.skillverify.authservice.repository.UserRepository;
import com.skillverify.authservice.utils.Role;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(()->new UserNotFoundException());
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(), 
				List.of(new SimpleGrantedAuthority(user.getRole().name()))
				);
	}
	
	
	public AdminResponseDto registerUser(AdminAddReqDto addReqDto) {
	    if (userRepository.existsByEmail(addReqDto.getEmail())) {
	        throw new UserAlreadyExistsException("Email already registered");
	    }

	    User user = new User();
	    user.setEmail(addReqDto.getEmail());
	    user.setPassword(passwordEncoder.encode(addReqDto.getPassword())); // ✅ Hashing
	    user.setRole(Role.ADMIN);
	    userRepository.save(user);

	    AdminResponseDto response = new AdminResponseDto();
	    response.setMessage("Admin registered successfully");
	    response.setEmail(user.getEmail());
	    return response;
	}}
