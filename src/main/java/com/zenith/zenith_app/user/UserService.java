package com.zenith.zenith_app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO register(RegisterRequest registerRequest){
        User user = User.builder()
                .firstName(registerRequest.firstName())
                .username(registerRequest.username())
                .email(registerRequest.email())
                .birthday(registerRequest.birthday())
                .password(registerRequest.password()) // TODO: hash password with BCrypt before saving - Z-3 Security
                .build();

        return UserDTO.fromUser(userRepository.save(user));
    }

    public UserDTO viewProfile(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(UserDTO::fromUser).orElseThrow(() -> new RuntimeException("User does not exist."));
    }

    public UserDTO updateProfile(Long userId, UpdateProfileRequest updateProfileRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User does not exist."));

        user.setFirstName(updateProfileRequest.firstName());
        user.setUsername(updateProfileRequest.username());
        user.setEmail(updateProfileRequest.email());

        return UserDTO.fromUser(userRepository.save(user));
    }

}
