package com.zenith.zenith_app.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public UserDTO register(@RequestBody RegisterRequest registerRequest) {
    return userService.register(registerRequest);
  }

  @GetMapping("/{userId}")
  public UserDTO viewProfile(@PathVariable Long userId) {
    return userService.viewProfile(userId);
  }

  @PutMapping("/{userId}")
  public UserDTO updateProfile(
      @PathVariable Long userId, @RequestBody UpdateProfileRequest updateProfileRequest) {
    return userService.updateProfile(userId, updateProfileRequest);
  }
}
