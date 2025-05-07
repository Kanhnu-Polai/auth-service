package com.skillverify.authservice.dto;

import com.skillverify.authservice.utils.Role;

import lombok.Data;

@Data
public class SignUpRequestDto {
	
	private String name;
	private String email;
	private String password;
	private Role role;

}
