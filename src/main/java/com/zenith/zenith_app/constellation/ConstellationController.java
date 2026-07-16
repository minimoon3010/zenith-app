package com.zenith.zenith_app.constellation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/constellation")
@RequiredArgsConstructor
public class ConstellationController {

  private final ConstellationService constellationService;

  @PostMapping("/new")
  public ConstellationDTO createConstellation(
      @Valid @RequestBody CreateConstellationRequest constellationRequest,
      @AuthenticationPrincipal UserDetails userDetails) {
    return constellationService.createConstellation(
        constellationRequest, userDetails.getUsername());
  }

  @GetMapping("/view/id/{id}")
  public ConstellationDTO viewConstellationById(
      @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    return constellationService.viewConstellationById(id, userDetails.getUsername());
  }

  @GetMapping("/view/name/{missionName}")
  public List<ConstellationDTO> viewConstellationByName(
      @PathVariable String missionName, @AuthenticationPrincipal UserDetails userDetails) {
    return constellationService.viewConstellationByName(missionName, userDetails.getUsername());
  }

  @GetMapping("/view/all")
  public List<ConstellationDTO> viewAllConstellations(
      @AuthenticationPrincipal UserDetails userDetails) {
    return constellationService.viewAllConstellations(userDetails.getUsername());
  }

  @PutMapping("/update/{id}")
  public ConstellationDTO updateConstellation(
      @RequestBody UpdateConstellationRequest updateConstellationRequest,
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {
    return constellationService.updateConstellation(
        updateConstellationRequest, id, userDetails.getUsername());
  }

  @DeleteMapping("/delete/id/{id}")
  public void deleteConstellationById(
      @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
    constellationService.deleteConstellationById(id, userDetails.getUsername());
  }
}
