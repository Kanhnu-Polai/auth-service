package com.skillverify.authservice.security.jwtutils;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillverify.authservice.errorcodeenum.ErrorCodeEnum;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		
		Map<String, Object> responseBody = Map.of(
                "errorCode", ErrorCodeEnum.INVALID_CREDENTIALS.getCode(),
                "message", ErrorCodeEnum.INVALID_CREDENTIALS.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
		 new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
		
	}

}
