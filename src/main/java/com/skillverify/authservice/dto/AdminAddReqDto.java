package com.skillverify.authservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AdminAddReqDto {
	private String email;
	private String password;

}
