package com.securetest.dto;
import com.securetest.model.Role;
import jakarta.validation.constraints.*;

public class RegisterRequest {
    @NotBlank(message = "Name is required") private String name;
    @NotBlank(message = "Email is required") @Email(message = "Invalid email") private String email;
    @NotBlank(message = "Password is required") @Size(min = 6, message = "Min 6 chars") private String password;
    @NotNull(message = "Role is required") private Role role;

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
}
