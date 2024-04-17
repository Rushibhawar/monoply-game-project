package com.game.app.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="player")
public class Player {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "player_name", length = 50, nullable = false)
    private String playerName;

    @Column(name = "player_email", length = 50, nullable = false, unique = true)
    private String playerEmail;

    @Column(name = "cash_balance")
    private int cashBalance;

    @Column(name = "current_position")
    private int currentPosition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game; // Player's current game

    @Column(name = "is_host")
    private boolean isHost;

}