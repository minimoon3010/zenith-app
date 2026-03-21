package com.zenith.zenith_app.star;

import com.zenith.zenith_app.config.ZenithConstants;
import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.constellation.ConstellationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarService {

    private final StarRepository starRepository;

    private final ConstellationRepository constellationRepository;

    public StarDTO createStar(CreateStarRequest request, Long constellationId){
        Star star = Star.builder()
                .name(request.name())
                .description(request.description())
                .status(StarStatus.NOT_STARTED)
                .xp(ZenithConstants.STAR_XP)
                .constellation(constellationRepository.findById(constellationId)
                        .orElseThrow(() -> new RuntimeException("Constellation not found.")))
                .build();
        return StarDTO.fromStar(starRepository.save(star));
    }

    public StarDTO viewStarById(Long id){
        Optional<Star> star = starRepository.findById(id);
        return star.map(StarDTO::fromStar).orElseThrow(() -> new RuntimeException("Star does not exist."));
    }

    public List<StarDTO> viewStarByName(String starName) {
        return starRepository.findByName(starName)
                .stream()
                .map(StarDTO::fromStar)
                .collect(Collectors.toList());
    }

    public List<StarDTO> filterByStatus(StarStatus status){
        return starRepository.findByStatus(status)
                .stream()
                .map(StarDTO::fromStar)
                .collect(Collectors.toList());
    }

    public List<StarDTO> getAllStarsByConstellation(Long constellationId){
        return starRepository.findByConstellation(constellationRepository.findById(constellationId)
                        .orElseThrow(() -> new RuntimeException("Constellation not found.")))
                .stream()
                .map(StarDTO::fromStar)
                .collect(Collectors.toList());
    }

    public StarDTO updateStar(UpdateStarRequest request, Long id){
        Star star = starRepository.findById(id).orElseThrow(() -> new RuntimeException("Star does not exist."));

        star.setName(request.name());
        star.setDescription(request.description());
        star.setStatus(request.status());

        if (request.constellationId() != null){
            Constellation constellation = constellationRepository.findById(request.constellationId())
                    .orElseThrow(() -> new RuntimeException("Constellation not found."));
            star.setConstellation(constellation);
        }

        return StarDTO.fromStar(starRepository.save(star));
    }

    public void deleteStarById(Long id){
        Star star = starRepository.findById(id).orElseThrow(() -> new RuntimeException("Star does not exist."));
        starRepository.delete(star);
    }

    public void deleteStarByName(String starName){
        List<Star> star = starRepository.findByName(starName);
        if (!star.isEmpty()){
            starRepository.deleteAll(star);
        } else {
            throw new RuntimeException("Star with this name does not exist.");
        }
    }
}
