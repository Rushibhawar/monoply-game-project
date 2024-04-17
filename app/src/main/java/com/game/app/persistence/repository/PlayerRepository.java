package com.game.app.persistence.repository;

import com.game.app.persistence.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String>  {

    boolean existsByPlayerEmail(String email);
}
