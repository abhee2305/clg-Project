package com.securetest.dto;
import jakarta.validation.constraints.*;

public class LoginRequest {
    @NotBlank(message = "Email is required") @Email private String email;
    @NotBlank(message = "Password is required") private String password;

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
}
