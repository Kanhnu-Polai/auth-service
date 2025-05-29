package com.skillverify.authservice.dto;



public class ValidateAuth {
    private String email;
    private String role;

    public ValidateAuth() {} // No-arg constructor

    public ValidateAuth(String email, String role) { // ✅ Add this
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}