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
@Table(name="place")
public class Place {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "buy_price")
    private int buyPrice;

    @Column(name = "rent_price")
    private int rentPrice;

    @Column(name = "position") // Add this field for the position of the place
    private int position; // Index of the place

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Player owner; // Owner of the place
}