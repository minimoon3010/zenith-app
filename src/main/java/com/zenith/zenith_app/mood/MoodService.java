package com.zenith.zenith_app.mood;

import com.zenith.zenith_app.config.SecureEntity;
import com.zenith.zenith_app.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MoodService {

  private final MoodRepository moodRepository;

  private final UserRepository userRepository;

  private final SecureEntity secureEntity;

  public MoodDTO createMood(CreateMoodRequest request, String username) {

    LocalDateTime now = LocalDateTime.now();

    Mood mood =
        Mood.builder()
            .status(request.status())
            .energy(request.energy())
            .createdAt(now)
            .lastUpdated(now)
            .user(
                userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found.")))
            .build();

    return MoodDTO.fromMood(moodRepository.save(mood));
  }

  public List<MoodDTO> viewAllMood(String username) {
    return moodRepository.findByUser_Username(username).stream()
        .map(MoodDTO::fromMood)
        .collect(Collectors.toList());
  }

  public MoodDTO viewMoodById(Long id, String username) {
    Mood mood = secureEntity.getSecureMood(id, username);
    return MoodDTO.fromMood(mood);
  }

  public List<MoodDTO> filterMoodByStatus(Integer status, String username) {
    return moodRepository.findByStatusAndUser_Username(status, username).stream()
        .map(MoodDTO::fromMood)
        .collect(Collectors.toList());
  }

  public List<MoodDTO> filterByTimestamp(
      LocalDateTime earlier, LocalDateTime later, String username) {
    return moodRepository
        .findByLastUpdatedBetweenAndUser_Username(earlier, later, username)
        .stream()
        .map(MoodDTO::fromMood)
        .collect(Collectors.toList());
  }

  public MoodDTO updateMood(UpdateMoodRequest request, Long id, String username) {
    Mood mood = secureEntity.getSecureMood(id, username);

    if (request.status() != null) {
      mood.setStatus(request.status());
    }
    if (request.energy() != null) {
      mood.setEnergy(request.energy());
    }
    mood.setLastUpdated(LocalDateTime.now());

    return MoodDTO.fromMood(moodRepository.save(mood));
  }
}
