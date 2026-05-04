package com.zenith.zenith_app.auth;

import com.zenith.zenith_app.user.User;
import com.zenith.zenith_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  private final JWTUtil jwtUtil;

  private final PasswordEncoder passwordEncoder;

  public JWTResponse login(LoginRequest loginRequest) {
    User userFound = findUserByIdentifier(loginRequest);

    if (passwordEncoder.matches(loginRequest.password(), userFound.getPassword())) {
      return new JWTResponse(jwtUtil.generateToken(userFound.getId()));
    } else {
      throw new BadCredentialsException("Passwords do not match.");
    }
  }

  private User findUserByIdentifier(LoginRequest loginRequest) {
    String username = loginRequest.username();
    String email = loginRequest.email();

    if (email == null && username == null) {
      throw new RuntimeException("Neither username nor email was provided.");
    }

    return (email != null
            ? userRepository.findByEmail(email)
            : userRepository.findByUsername(username))
        .orElseThrow(() -> new BadCredentialsException("User not found."));
  }
}
