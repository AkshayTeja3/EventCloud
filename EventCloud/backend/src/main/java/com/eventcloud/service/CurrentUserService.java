package com.eventcloud.service;
import com.eventcloud.domain.AppUser;
import com.eventcloud.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CurrentUserService {
 private final UserRepository users;
 public CurrentUserService(UserRepository users){this.users=users;}
 @Transactional(readOnly=true) public AppUser require(){String email=SecurityContextHolder.getContext().getAuthentication().getName();return users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalStateException("Authenticated user no longer exists"));}
}
