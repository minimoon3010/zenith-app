package com.zenith.zenith_app.constellation;

import com.zenith.zenith_app.star.Star;
import com.zenith.zenith_app.user.User;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "z_constellation")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Constellation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String missionName;

  private String objective;

  @OneToMany(mappedBy = "constellation")
  private List<Star> stars;

  private boolean isXpAwarded = false;

  private ConstellationStatus status = ConstellationStatus.IN_PROGRESS;
}
