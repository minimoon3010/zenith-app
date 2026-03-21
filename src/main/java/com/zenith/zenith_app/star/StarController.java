package com.zenith.zenith_app.star;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/star")
@RequiredArgsConstructor
public class StarController {

    private final StarService starService;

    @PostMapping("/new")
    public StarDTO createStar(@RequestBody CreateStarRequest request){
        return starService.createStar(request, request.constellationId());
    }

    @GetMapping("/view/id/{starId}")
    public StarDTO viewStarById(@PathVariable Long starId){
        return starService.viewStarById(starId);
    }

    @GetMapping("/view/name/{starName}")
    public List<StarDTO> viewStarByName(@PathVariable String starName){
        return starService.viewStarByName(starName);
    }

    @GetMapping("/view/status/{status}")
    public List<StarDTO> filterStarByStatus(@PathVariable StarStatus status){
        return starService.filterByStatus(status);
    }

    @GetMapping("/view/constellation/{constellationId}")
    public List<StarDTO> getAllStarsFromConstellation(@PathVariable Long constellationId){
        return starService.getAllStarsByConstellation(constellationId);
    }

    @PutMapping("/update/{starId}")
    public StarDTO updateStar(@RequestBody UpdateStarRequest request, @PathVariable Long starId){
        return starService.updateStar(request, starId);
    }

    @DeleteMapping("/delete/id/{starId}")
    public void deleteStarById(@PathVariable Long starId){
        starService.deleteStarById(starId);
    }

    @DeleteMapping("/delete/name/{starName}")
    public void deleteStarByName(@PathVariable String starName){
        starService.deleteStarByName(starName);
    }
}
