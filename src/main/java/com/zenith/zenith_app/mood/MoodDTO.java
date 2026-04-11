package com.zenith.zenith_app.mood;

import java.time.LocalDateTime;

public record MoodDTO(
    Long id, Integer status, String energy, LocalDateTime createdAt, LocalDateTime lastUpdated) {
  public static MoodDTO fromMood(Mood mood) {
    return new MoodDTO(
        mood.getId(),
        mood.getStatus(),
        mood.getEnergy(),
        mood.getCreatedAt(),
        mood.getLastUpdated());
  }
}
