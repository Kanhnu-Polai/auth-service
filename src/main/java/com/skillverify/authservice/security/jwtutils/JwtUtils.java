package com.skillverify.authservice.security.jwtutils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.skillverify.authservice.utils.Role;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

	// The secret must be at least 256 bits (32 chars) for HS256
	@Value("${jwt.secret}")
	private String secret;

	private Key key;

	@Value("${jwt.expiration}")
	private long expiration;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(UserDetails userDetails, Role role) {
		log.info("Generating token for user: {}", userDetails.getUsername());
		return Jwts
				.builder()
				.setSubject(userDetails.getUsername())
				.claim("role", role)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration)) // 10 hours
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		try {
			String username = Jwts
					.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
			log.info("Extracted username from token: {}", username);
			return username;
		} catch (JwtException e) {
			log.error("Failed to extract username from token: {}", e.getMessage());
			return null;
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody()
					.getExpiration();
			boolean expired = expiration.before(new Date());
			log.info("Token expired: {}", expired);
			return expired;
		} catch (JwtException e) {
			log.error("Failed to check token expiration: {}", e.getMessage());
			return true;
		}
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}
}