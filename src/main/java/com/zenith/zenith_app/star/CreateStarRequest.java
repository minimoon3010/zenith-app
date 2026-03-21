package com.zenith.zenith_app.star;

public record CreateStarRequest(
        String name,
        String description,
        Long constellationId) {
}
