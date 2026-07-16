package com.zenith.zenith_app.constellation;

public record UpdateConstellationRequest(
    String missionName, String objective, ConstellationStatus status) {}
