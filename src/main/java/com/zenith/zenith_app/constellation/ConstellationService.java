package com.zenith.zenith_app.constellation;

import com.zenith.zenith_app.config.SecureEntity;
import com.zenith.zenith_app.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConstellationService {

  private final ConstellationRepository constellationRepository;

  private final UserRepository userRepository;

  private final SecureEntity secureEntity;

  public ConstellationDTO createConstellation(CreateConstellationRequest request, String username) {
    Constellation constellation =
        Constellation.builder()
            .missionName(request.missionName())
            .objective(request.objective())
            .user(
                userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found.")))
            .build();

    return ConstellationDTO.fromConstellation(constellationRepository.save(constellation));
  }

  public ConstellationDTO viewConstellationById(Long id, String username) {
    Constellation constellation = secureEntity.getSecureConstellation(id, username);
    return ConstellationDTO.fromConstellation(constellation);
  }

  public List<ConstellationDTO> viewConstellationByName(String missionName, String username) {
    return constellationRepository.findByMissionNameAndUser_Username(missionName, username).stream()
        .map(ConstellationDTO::fromConstellation)
        .toList();
  }

  public ConstellationDTO updateConstellation(
      UpdateConstellationRequest request, Long id, String username) {
    Constellation constellation = secureEntity.getSecureConstellation(id, username);

    if (request.missionName() != null) {
      constellation.setMissionName(request.missionName());
    }

    if (request.objective() != null) {
      constellation.setObjective(request.objective());
    }

    return ConstellationDTO.fromConstellation(constellationRepository.save(constellation));
  }

  public List<ConstellationDTO> viewAllConstellations(String username) {
    return constellationRepository.findByUser_Username(username).stream()
        .map(ConstellationDTO::fromConstellation)
        .toList();
  }

  public void deleteConstellationById(Long id, String username) {
    Constellation constellation = secureEntity.getSecureConstellation(id, username);
    constellationRepository.delete(constellation);
  }
}
