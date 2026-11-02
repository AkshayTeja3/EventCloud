package com.eventcloud.domain;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="app_users", indexes=@Index(name="idx_user_email", columnList="email", unique=true))
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @Column(nullable=false, unique=true, length=254) private String email;
 @Column(nullable=false) private String passwordHash;
 @Column(nullable=false, length=100) private String fullName;
 @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Role role=Role.USER;
 @Column(nullable=false) private Instant createdAt=Instant.now();
 protected AppUser(){}
 public AppUser(String email,String passwordHash,String fullName,Role role){this.email=email;this.passwordHash=passwordHash;this.fullName=fullName;this.role=role;}
 public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
 public String getFullName(){return fullName;} public Role getRole(){return role;} public Instant getCreatedAt(){return createdAt;}
 public void setRole(Role role){this.role=role;}
}
