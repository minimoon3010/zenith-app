package com.zenith.zenith_app.constellation;

import jakarta.validation.constraints.NotBlank;

public record CreateConstellationRequest(@NotBlank String missionName, String objective) {}
