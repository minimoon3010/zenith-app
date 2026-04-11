package com.zenith.zenith_app.star;

public record StarDTO(
    Long id, String name, String description, StarStatus status, int xp, Long constellationId) {
  public static StarDTO fromStar(Star star) {
    return new StarDTO(
        star.getId(),
        star.getName(),
        star.getDescription(),
        star.getStatus(),
        star.getXp(),
        star.getConstellation().getId());
  }
}
