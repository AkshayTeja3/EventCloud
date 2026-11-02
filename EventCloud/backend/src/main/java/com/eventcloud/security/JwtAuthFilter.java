package com.eventcloud.security;
import com.eventcloud.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UserRepository users;
 public JwtAuthFilter(JwtService jwt,UserRepository users){this.jwt=jwt;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws ServletException,IOException{
  String header=request.getHeader("Authorization");
  if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
   try {var claims=jwt.parse(header.substring(7));var user=users.findByEmailIgnoreCase(claims.getSubject()).orElse(null);
    if(user!=null){var auth=new UsernamePasswordAuthenticationToken(user.getEmail(),null,List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name())));SecurityContextHolder.getContext().setAuthentication(auth);}
   } catch(RuntimeException ignored){ SecurityContextHolder.clearContext(); }
  }
  chain.doFilter(request,response);
 }
}
