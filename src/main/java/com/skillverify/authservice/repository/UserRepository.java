package com.skillverify.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillverify.authservice.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	// Custom query method to find a user by email
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);

}
