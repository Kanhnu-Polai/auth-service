package com.skillverify.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.skillverify.authservice.dto.AdminAddReqDto;
import com.skillverify.authservice.dto.AdminResponseDto;
import com.skillverify.authservice.service.UserDetailsServiceImpl;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // ✅ Only accessible by ADMIN
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDto> addNewAdmin(@RequestBody AdminAddReqDto dto) {
        AdminResponseDto response = userDetailsService.registerUser(dto);
        return ResponseEntity.ok(response);
    }
}