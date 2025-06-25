// package com.example.userservice.security;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// import java.util.Collections;

// @Component
// @Slf4j
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     private final RestTemplate restTemplate = new RestTemplate();
//     private static final String AUTH_SERVICE_VALIDATE_URL = "http://localhost:8082/v1/auth/validate";

//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain) throws ServletException, IOException {

//         String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         String token = authHeader.substring(7);

//         try {
//             Boolean isValid = restTemplate.getForObject(
//                     AUTH_SERVICE_VALIDATE_URL + "?Authorization=Bearer%20" + token, Boolean.class);

//             if (Boolean.TRUE.equals(isValid)) {
//                 UsernamePasswordAuthenticationToken authentication =
//                         new UsernamePasswordAuthenticationToken("authenticatedUser", null, Collections.emptyList());
//                 authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//                 log.debug("JWT token validated and authentication set");
//             } else {
//                 log.warn("JWT token validation failed");
//                 response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                 return;
//             }
//         } catch (Exception ex) {
//             log.error("JWT validation error: {}", ex.getMessage());
//             response.setStatus(HttpStatus.UNAUTHORIZED.value());
//             return;
//         }

//         filterChain.doFilter(request, response);
//     }
// }
