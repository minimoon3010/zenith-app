package com.zenith.zenith_app.user;

import java.time.LocalDate;

public record RegisterRequest(
    String firstName, String username, String email, String password, LocalDate birthday) {}
