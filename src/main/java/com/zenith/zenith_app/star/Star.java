package com.zenith.zenith_app.star;

import com.zenith.zenith_app.constellation.Constellation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    private StarStatus status;

    private int xp;

    @ManyToOne
    @JoinColumn(name = "constellation_id")
    private Constellation constellation;

}
