package com.zenith.zenith_app.auth;

import com.zenith.zenith_app.user.User;
import com.zenith.zenith_app.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;

  private final UserDetailsService userDetailsService;

  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {

      String token = authHeader.substring("Bearer ".length());
      String subject = jwtUtil.extractSubject(token);

      User user =
          userRepository
              .findById(Long.parseLong(subject))
              .orElseThrow(() -> new RuntimeException("User not found"));

      UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
      createAuthToken(token, userDetails);
    }

    filterChain.doFilter(request, response);
  }

  private void createAuthToken(String token, UserDetails userDetails) {
    if (jwtUtil.validateToken(token)) {
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
  }
}
