package com.zenith.zenith_app.star;

import com.zenith.zenith_app.constellation.Constellation;
import com.zenith.zenith_app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "z_star")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Star {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String description;

  @Enumerated(EnumType.STRING)
  private StarStatus status;

  private int xp;

  @ManyToOne
  @JoinColumn(name = "constellation_id")
  private Constellation constellation;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
