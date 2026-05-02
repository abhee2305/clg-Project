package com.securetest.dto;

public class AuthResponse {
    private String token, id, name, email, role, message;

    public AuthResponse() {}
    public AuthResponse(String token, String id, String name, String email, String role, String message) {
        this.token=token; this.id=id; this.name=name; this.email=email; this.role=role; this.message=message;
    }
    public String getToken() { return token; }
    public void setToken(String v) { this.token = v; }
    public String getId() { return id; }
    public void setId(String v) { this.id = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final AuthResponse r = new AuthResponse();
        public Builder token(String v) { r.token = v; return this; }
        public Builder id(String v) { r.id = v; return this; }
        public Builder name(String v) { r.name = v; return this; }
        public Builder email(String v) { r.email = v; return this; }
        public Builder role(String v) { r.role = v; return this; }
        public Builder message(String v) { r.message = v; return this; }
        public AuthResponse build() { return r; }
    }
}
