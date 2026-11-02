package com.eventcloud.config;
import com.eventcloud.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){
  return new BCryptPasswordEncoder();
 }
 @Bean
 SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthFilter jwt,
                                         @Value
                                                 ("${app.frontend-origin}") String origin)throws Exception{
  http.csrf(c->c.disable()).cors(c->c.configurationSource(req->
          {
           CorsConfiguration cfg=new CorsConfiguration();
           cfg.setAllowedOrigins(List.of(origin));
           cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
           cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
           cfg.setAllowCredentials(true);
           return cfg;
          }
          ))
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/**","/uploads/**","/actuator/health").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/events/managed").authenticated()
    .requestMatchers(HttpMethod.GET,"/api/events","/api/events/*").permitAll()
    .anyRequest().authenticated())
   .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
}
