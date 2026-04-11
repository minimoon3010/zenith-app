package com.zenith.zenith_app.user;

import java.time.LocalDate;

public record UserDTO(
    Long id,
    String firstName,
    String username,
    String email,
    LocalDate birthday,
    int level,
    int xp) {
  public static UserDTO fromUser(User user) {
    return new UserDTO(
        user.getId(),
        user.getFirstName(),
        user.getUsername(),
        user.getEmail(),
        user.getBirthday(),
        user.getLevel(),
        user.getXp());
  }
}
