package com.eventcloud.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Service
public class JwtService {
 private final SecretKey key;
 private final long expirationMs;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expirationMs){
  if(secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalArgumentException("JWT_SECRET must contain at least 32 bytes");
  this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expirationMs=expirationMs;
 }
 public String generate(String email,String role){Date now=new Date();return Jwts.builder().setSubject(email).claim("role",role).setIssuedAt(now).setExpiration(new Date(now.getTime()+expirationMs)).signWith(key,SignatureAlgorithm.HS256).compact();}
 public Claims parse(String token){return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();}
}
