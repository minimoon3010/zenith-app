package com.zenith.zenith_app.constellation;

import java.time.LocalDateTime;

public record ConstellationDTO(
    Long id,
    Long userId,
    String missionName,
    String objective,
    LocalDateTime createdAt,
    LocalDateTime lastUpdated) {
  public static ConstellationDTO fromConstellation(Constellation constellation) {
    return new ConstellationDTO(
        constellation.getId(),
        constellation.getUser().getId(),
        constellation.getMissionName(),
        constellation.getObjective(),
        constellation.getCreatedAt(),
        constellation.getLastUpdated());
  }
}
