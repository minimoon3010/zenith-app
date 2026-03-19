package com.zenith.zenith_app.constellation;

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
    public ConstellationDTO createConstellation(@RequestBody CreateConstellationRequest constellationRequest,
                                                @AuthenticationPrincipal UserDetails userDetails){
        return constellationService.createConstellation(constellationRequest, userDetails.getUsername());
    }

    @GetMapping("/{id}")
    public ConstellationDTO viewConstellationById(@PathVariable Long id){
        return constellationService.viewConstellationById(id);
    }

    @GetMapping
    public ConstellationDTO viewConstellationByName(@RequestParam String missionName){
        return constellationService.viewConstellationByName(missionName);
    }

    @PutMapping("/{id}")
    public ConstellationDTO updateConstellation(@RequestBody UpdateConstellationRequest updateConstellationRequest, @PathVariable Long id){
        return constellationService.updateConstellation(updateConstellationRequest, id);
    }

    @DeleteMapping("/{id}")
    public void deleteConstellationById(@PathVariable Long id){
        constellationService.deleteConstellationById(id);
    }

    @DeleteMapping
    public void deleteConstellationByName(@RequestParam String missionName){
        constellationService.deleteConstellationByName(missionName);
    }

}
