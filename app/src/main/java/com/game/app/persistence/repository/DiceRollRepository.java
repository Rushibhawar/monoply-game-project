package com.game.app.persistence.repository;

import com.game.app.persistence.model.DiceRoll;
import com.game.app.persistence.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiceRollRepository extends JpaRepository<DiceRoll, String> {
}
