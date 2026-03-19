package com.zenith.zenith_app.constellation;

import com.zenith.zenith_app.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

//    @OneToMany(mappedBy = "constellation")
//    private List<Star> stars;

}
