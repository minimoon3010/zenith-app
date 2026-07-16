package com.zenith.zenith_app.star;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/star")
@RequiredArgsConstructor
public class StarController {

  private final StarService starService;

  @PostMapping("/new")
  public StarDTO createStar(
      @RequestBody CreateStarRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    return starService.createStar(request, request.constellationId(), userDetails.getUsername());
  }

  @GetMapping("/view/id/{starId}")
  public StarDTO viewStarById(
      @PathVariable Long starId, @AuthenticationPrincipal UserDetails userDetails) {
    return starService.viewStarById(starId, userDetails.getUsername());
  }

  @GetMapping("/view/name")
  public List<StarDTO> viewStarByName(
      @RequestParam String starName, @AuthenticationPrincipal UserDetails userDetails) {
    return starService.viewStarByName(starName, userDetails.getUsername());
  }

  @GetMapping("/view/status/{status}")
  public List<StarDTO> filterStarByStatus(
      @PathVariable StarStatus status, @AuthenticationPrincipal UserDetails userDetails) {
    return starService.filterByStatus(status, userDetails.getUsername());
  }

  @GetMapping("/view/constellation/{constellationId}")
  public List<StarDTO> getAllStarsFromConstellation(
      @PathVariable Long constellationId, @AuthenticationPrincipal UserDetails userDetails) {
    return starService.getAllStarsByConstellation(constellationId, userDetails.getUsername());
  }

  @PutMapping("/update/{starId}")
  public StarDTO updateStar(
      @RequestBody UpdateStarRequest request,
      @PathVariable Long starId,
      @AuthenticationPrincipal UserDetails userDetails) {
    return starService.updateStar(request, starId, userDetails.getUsername());
  }

  @DeleteMapping("/delete/id/{starId}")
  public void deleteStarById(
      @PathVariable Long starId, @AuthenticationPrincipal UserDetails userDetails) {
    starService.deleteStarById(starId, userDetails.getUsername());
  }

  @DeleteMapping("/delete/name/{starName}")
  public void deleteStarByName(
      @PathVariable String starName, @AuthenticationPrincipal UserDetails userDetails) {
    starService.deleteStarByName(starName, userDetails.getUsername());
  }
}
