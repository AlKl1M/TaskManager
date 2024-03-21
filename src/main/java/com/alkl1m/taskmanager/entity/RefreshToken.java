package com.alkl1m.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(schema = "taskmanager", name = "t_refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "c_user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "c_token")
    private String token;

    @Column(name = "c_expiry_date")
    private Instant expiryDate;
}
