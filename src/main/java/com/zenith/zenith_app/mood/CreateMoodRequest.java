package com.zenith.zenith_app.mood;

import jakarta.validation.constraints.NotNull;

public record CreateMoodRequest(@NotNull Integer status, String energy) {}
