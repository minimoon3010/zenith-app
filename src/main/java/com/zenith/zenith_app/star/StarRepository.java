package com.zenith.zenith_app.star;

import com.zenith.zenith_app.constellation.Constellation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {

  List<Star> findByNameAndUser_Username(String name, String username);

  List<Star> findByStatusAndUser_Username(StarStatus status, String username);

  List<Star> findByConstellation(Constellation constellationId);
}
