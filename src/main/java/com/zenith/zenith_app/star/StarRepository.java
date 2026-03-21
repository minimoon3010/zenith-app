package com.zenith.zenith_app.star;

import com.zenith.zenith_app.constellation.Constellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {

    List<Star> findByName(String name);

    List<Star> findByStatus(StarStatus status);

    List<Star> findByConstellation(Constellation constellationId);
}
