package com.game.app.services.serviceImpl;

import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.GameRepository;
import com.game.app.persistence.repository.PlayerRepository;
import com.game.app.services.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public Game createGame(Player hostPlayer, Player player2) {
        // Find all existing active games associated with the host player
        List<Game> existingGames = gameRepository.findAllActiveGamesByPlayer1IdAndIsActiveTrue(hostPlayer.getId());

        // Soft delete all existing active games
        for (Game existingGame : existingGames) {
            existingGame.setActive(false); // Mark the game as inactive
        }
        gameRepository.saveAll(existingGames); // Update all existing game entities

        // Create a new game entity with the host player as player1 and second player as player2
        Game newGame = Game.builder()
                .id(UUID.randomUUID().toString())
                .player1(hostPlayer)
                .player2(player2)
                .isActive(true)
                .createdByPlayerId(hostPlayer.getId())
                .turnCount(0)
                .build();

        hostPlayer.setCashBalance(1000);
        player2.setCashBalance(1000);

        // Save the new game entity and update player entities
        Game savedGame = gameRepository.save(newGame);
        playerRepository.save(hostPlayer);
        playerRepository.save(player2);

        // Save the new game entity
        return savedGame;
    }
}
