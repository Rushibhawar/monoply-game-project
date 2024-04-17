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
@Table(name="dice_roll")
public class DiceRoll {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "playerId")
    private Player player;

    @Column(name = "dice_1_value")
    private int dice1Value;

    @Column(name = "dice_2_value")
    private int dice2Value;

    @Column(name = "total_value")
    private int totalValue;

}