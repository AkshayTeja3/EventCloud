package com.eventcloud.repository;
import com.eventcloud.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<AppUser,UUID> { Optional<AppUser> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email); }
