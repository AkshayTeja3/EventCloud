package com.eventcloud.service;
import com.eventcloud.domain.*;
import com.eventcloud.dto.AuthDtos.*;
import com.eventcloud.repository.UserRepository;
import com.eventcloud.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthService {
 private final UserRepository users;private final PasswordEncoder encoder;private final JwtService jwt;
 public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 @Transactional public AuthResponse register(RegisterRequest r){
  String email=r.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
  AppUser u=users.save(new AppUser(email,encoder.encode(r.password()),r.fullName().trim(),Role.USER));return response(u);
 }
 @Transactional(readOnly=true) public AuthResponse login(LoginRequest r){
  AppUser u=users.findByEmailIgnoreCase(r.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
  if(!encoder.matches(r.password(),u.getPasswordHash()))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");return response(u);
 }
 private AuthResponse response(AppUser u){return new AuthResponse(jwt.generate(u.getEmail(),u.getRole().name()),"Bearer",u.getId().toString(),u.getFullName(),u.getEmail(),u.getRole().name());}
}
