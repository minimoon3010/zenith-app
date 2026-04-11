package com.zenith.zenith_app.mood;

import com.zenith.zenith_app.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "z_mood")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Mood {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Min(1)
  @Max(10)
  private Integer status;

  private String energy;

  private LocalDateTime createdAt;

  private LocalDateTime lastUpdated;
}
