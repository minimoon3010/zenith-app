package com.zenith.zenith_app.user;

public record UpdateProfileRequest(
    String firstName, String username, String email, String password) {}
