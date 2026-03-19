package com.zenith.zenith_app.constellation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConstellationRepository extends JpaRepository<Constellation, Long> {

    Optional<Constellation> findByMissionName(String missionName);

}
