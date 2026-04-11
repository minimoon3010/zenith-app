package com.zenith.zenith_app.mood;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

  private final MoodService moodService;

  @PostMapping("/new")
  public MoodDTO createMood(
      @Valid @RequestBody CreateMoodRequest request,
      @AuthenticationPrincipal UserDetails userDetails) {
    return moodService.createMood(request, userDetails.getUsername());
  }

  @PutMapping("/update/{id}")
  public MoodDTO updateMood(
      @Valid @RequestBody UpdateMoodRequest request,
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {
    return moodService.updateMood(request, id, userDetails.getUsername());
  }

  @GetMapping("/view/all")
  public List<MoodDTO> viewAllMood(@AuthenticationPrincipal UserDetails userDetails) {
    return moodService.viewAllMood(userDetails.getUsername());
  }

  @GetMapping("/view/id/{id}")
  public MoodDTO viewMoodById(
      @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    return moodService.viewMoodById(id, userDetails.getUsername());
  }

  @GetMapping("/filter/status")
  public List<MoodDTO> filterMoodByStatus(
      @RequestParam Integer status, @AuthenticationPrincipal UserDetails userDetails) {
    return moodService.filterMoodByStatus(status, userDetails.getUsername());
  }

  @GetMapping("/filter/time")
  public List<MoodDTO> filterMoodByTime(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime earlier,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime later,
      @AuthenticationPrincipal UserDetails userDetails) {
    return moodService.filterByTimestamp(earlier, later, userDetails.getUsername());
  }
}
