package com.zenith.zenith_app.constellation;

import com.zenith.zenith_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConstellationService {

    private final ConstellationRepository constellationRepository;

    private final UserRepository userRepository;

    public ConstellationDTO createConstellation(CreateConstellationRequest request, String username) {
        Constellation constellation = Constellation.builder()
                .missionName(request.missionName())
                .objective(request.objective())
                .user(userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Username not found.")))
                .build();

        return ConstellationDTO.fromConstellation(constellationRepository.save(constellation));
    }

    public ConstellationDTO viewConstellationById(Long id){
        Optional<Constellation> optionalConstellation = constellationRepository.findById(id);
        return optionalConstellation.map(ConstellationDTO::fromConstellation)
                .orElseThrow(() -> new RuntimeException("Constellation does not exist."));
    }

    public ConstellationDTO viewConstellationByName(String missionName) {
        Optional<Constellation> optionalConstellation = constellationRepository.findByMissionName(missionName);
        return optionalConstellation.map(ConstellationDTO::fromConstellation)
                .orElseThrow(() -> new RuntimeException("Constellation does not exist."));
        // TODO: should return List<ConstellationDTO> - see defect issue
    }

    public ConstellationDTO updateConstellation(UpdateConstellationRequest request, Long id){
        Constellation constellation = constellationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Constellation does not exist."));

        constellation.setMissionName(request.missionName());
        constellation.setObjective(request.objective());

        return ConstellationDTO.fromConstellation(constellationRepository.save(constellation));
    }

    public List<ConstellationDTO> viewAllConstellations(String username){
        return constellationRepository.findByUser_Username(username)
                .stream()
                .map(ConstellationDTO::fromConstellation)
                .collect(Collectors.toList());
    }

    public void deleteConstellationById(Long id){
        Constellation constellation = constellationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Constellation does not exist."));
        constellationRepository.delete(constellation);
    }

    public void deleteConstellationByName(String missionName){
        Constellation constellation = constellationRepository.findByMissionName(missionName)
                .orElseThrow(() -> new RuntimeException("Constellation does not exist."));
        constellationRepository.delete(constellation);
    }

}
