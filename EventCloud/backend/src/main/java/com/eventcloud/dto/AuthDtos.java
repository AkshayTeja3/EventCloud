package com.eventcloud.dto;
import jakarta.validation.constraints.*;
public class AuthDtos {
 public record RegisterRequest(@NotBlank @Size(max=100) String fullName,@Email @NotBlank @Size(max=254) String email,@NotBlank @Size(min=8,max=72) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record AuthResponse(String token,String tokenType,String id,String fullName,String email,String role){}
}
