package com.zenith.zenith_app.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public UserDTO register(RegisterRequest registerRequest) {
    User user =
        User.builder()
            .firstName(registerRequest.firstName())
            .username(registerRequest.username())
            .email(registerRequest.email())
            .birthday(registerRequest.birthday())
            .password(passwordEncoder.encode(registerRequest.password()))
            .build();

    return UserDTO.fromUser(userRepository.save(user));
  }

  public UserDTO viewProfile(Long userId) {
    Optional<User> optionalUser = userRepository.findById(userId);
    return optionalUser
        .map(UserDTO::fromUser)
        .orElseThrow(() -> new RuntimeException("User does not exist."));
  }

  public UserDTO updateProfile(Long userId, UpdateProfileRequest updateProfileRequest) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("User does not exist."));

    if (updateProfileRequest.firstName() != null) {
      user.setFirstName(updateProfileRequest.firstName());
    }

    if (updateProfileRequest.username() != null) {
      user.setUsername(updateProfileRequest.username());
    }

    if (updateProfileRequest.email() != null) {
      user.setEmail(updateProfileRequest.email());
    }

    return UserDTO.fromUser(userRepository.save(user));
  }
}
