package com.zenith.zenith_app.mood;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {

  List<Mood> findByStatusAndUser_Username(Integer status, String username);

  List<Mood> findByLastUpdatedBetweenAndUser_Username(
      LocalDateTime earlier, LocalDateTime later, String username);

  List<Mood> findByUser_Username(String username);
}
