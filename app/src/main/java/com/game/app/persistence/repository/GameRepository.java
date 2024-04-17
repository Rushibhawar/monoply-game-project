package com.game.app.persistence.repository;

import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
}
