package com.zenith.zenith_app.star;

public record UpdateStarRequest(
    String name, String description, StarStatus status, Long constellationId) {}
