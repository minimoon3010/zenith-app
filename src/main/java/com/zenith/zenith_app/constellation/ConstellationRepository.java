package com.zenith.zenith_app.constellation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, Long> {

  List<Constellation> findByMissionNameAndUser_Username(String missionName, String username);

  List<Constellation> findByUser_Username(String username);
}
