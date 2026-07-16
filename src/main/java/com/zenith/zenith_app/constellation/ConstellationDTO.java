package com.zenith.zenith_app.constellation;

public record ConstellationDTO(Long id, Long userId, String missionName, String objective) {
  public static ConstellationDTO fromConstellation(Constellation constellation) {
    return new ConstellationDTO(
        constellation.getId(),
        constellation.getUser().getId(),
        constellation.getMissionName(),
        constellation.getObjective());
  }
}
