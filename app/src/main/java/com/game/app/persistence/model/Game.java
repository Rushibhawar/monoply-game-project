package com.game.app.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="game")
public class Game {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @ManyToOne
    @JoinColumn(name = "player_1_id")
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player_2_id")
    private Player player2;

    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private Player currentPlayer;

    @Column(name = "turn_count")
    private int turnCount;

    @Column(name = "winner_id")
    private String winnerId; // PlayerId of the winner (null until game ends)
}