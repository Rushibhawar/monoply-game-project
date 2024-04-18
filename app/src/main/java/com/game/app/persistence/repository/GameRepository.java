package com.game.app.persistence.repository;

import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    List<Game> findAllActiveGamesByPlayer1IdAndIsActiveTrue(String hostPlayerId);
    List<Game> findAllByIsActiveTrue();

}
