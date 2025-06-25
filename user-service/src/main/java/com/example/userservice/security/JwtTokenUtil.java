// package com.example.userservice.security;

// import io.jsonwebtoken.*;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import java.security.Key;
// import java.util.Date;

// @Component
// public class JwtTokenUtil {

//     @Value("${jwt.secret}")
//     private String secret;

//     private Key key;

//     @PostConstruct
//     public void init() {
//         this.key = Keys.hmacShaKeyFor(secret.getBytes());
//     }

//     public boolean validateToken(String token) {
//         try {
//             Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//             return true;
//         } catch (JwtException | IllegalArgumentException e) {
//             return false;
//         }
//     }

//     public String getEmailFromToken(String token) {
//         Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();

//         return claims.getSubject();
//     }

//     public String getRoleFromToken(String token) {
//         Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(key)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();

//         return claims.get("role", String.class);
//     }
// }
