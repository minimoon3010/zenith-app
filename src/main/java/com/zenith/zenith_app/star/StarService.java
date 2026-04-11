package com.zenith.zenith_app.star;

import com.zenith.zenith_app.config.SecureEntity;
import com.zenith.zenith_app.config.ZenithConstants;
import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StarService {

  private final StarRepository starRepository;

  private final UserRepository userRepository;

  private final SecureEntity secureEntity;

  public StarDTO createStar(CreateStarRequest request, Long constellationId, String username) {
    Star star =
        Star.builder()
            .name(request.name())
            .description(request.description())
            .status(StarStatus.NOT_STARTED)
            .xp(ZenithConstants.STAR_XP)
            .constellation(secureEntity.getSecureConstellation(constellationId, username))
            .user(
                userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found.")))
            .build();
    return StarDTO.fromStar(starRepository.save(star));
  }

  public StarDTO viewStarById(Long id, String username) {
    Star star = secureEntity.getSecureStar(id, username);
    return StarDTO.fromStar(star);
  }

  public List<StarDTO> viewStarByName(String starName, String username) {
    return starRepository.findByNameAndUser_Username(starName, username).stream()
        .map(StarDTO::fromStar)
        .toList();
  }

  public List<StarDTO> filterByStatus(StarStatus status, String username) {
    return starRepository.findByStatusAndUser_Username(status, username).stream()
        .map(StarDTO::fromStar)
        .toList();
  }

  public List<StarDTO> getAllStarsByConstellation(Long constellationId, String username) {
    return starRepository
        .findByConstellation(secureEntity.getSecureConstellation(constellationId, username))
        .stream()
        .map(StarDTO::fromStar)
        .toList();
  }

  public StarDTO updateStar(UpdateStarRequest request, Long id, String username) {
    Star star = secureEntity.getSecureStar(id, username);

    if (request.name() != null) {
      star.setName(request.name());
    }

    if (request.description() != null) {
      star.setDescription(request.description());
    }

    if (request.status() != null) {
      star.setStatus(request.status());
    }

    if (request.constellationId() != null) {
      Constellation constellation =
          secureEntity.getSecureConstellation(request.constellationId(), username);
      star.setConstellation(constellation);
    }

    return StarDTO.fromStar(starRepository.save(star));
  }

  public void deleteStarById(Long id, String username) {
    Star star = secureEntity.getSecureStar(id, username);
    starRepository.delete(star);
  }

  public void deleteStarByName(String starName, String username) {
    List<Star> star = starRepository.findByNameAndUser_Username(starName, username);
    if (!star.isEmpty()) {
      starRepository.deleteAll(star);
    } else {
      throw new RuntimeException("Star with this name does not exist.");
    }
  }
}
