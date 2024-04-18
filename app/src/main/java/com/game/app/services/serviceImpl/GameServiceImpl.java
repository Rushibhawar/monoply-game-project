package com.game.app.services.serviceImpl;

import com.game.app.controller.GameController;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Place;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.GameRepository;
import com.game.app.persistence.repository.PlaceRepository;
import com.game.app.persistence.repository.PlayerRepository;
import com.game.app.services.service.GameService;
import com.game.app.services.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceService placeService;

    Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    @Override
    public Game createGame(Player hostPlayer, Player player2) {
        // Find all existing active games associated with the host player
//        List<Game> existingGames = gameRepository.findAllActiveGamesByPlayer1IdAndIsActiveTrue(hostPlayer.getId());
        List<Game> existingGames = gameRepository.findAllByIsActiveTrue();

        // Soft delete all existing active games
        for (Game existingGame : existingGames) {
            existingGame.setActive(false); // Mark the game as inactive
        }
        gameRepository.saveAll(existingGames); // Update all existing game entities

        // Clear ownership of all places
        placeService.clearOwnershipOfAllPlaces();

        // Create a new game entity with the host player as player1 and second player as player2
        Game newGame = Game.builder()
                .id(UUID.randomUUID().toString())
                .player1(hostPlayer)
                .player2(player2)
                .currentPlayer(hostPlayer)
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

    @Override
    public String getPlaceLanded(String gameId, String playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

//        Player currentPlayer = game.getCurrentPlayer();
        Player currentPlayer = game.getPlayer1().getId().equals(playerId) ? game.getPlayer1() : game.getPlayer2();
        logger.info("current player to getPlaceLanded : {} ",currentPlayer);

        Place currentPlace = placeRepository.findByPosition(currentPlayer.getCurrentPosition())
                .orElseThrow(() -> new IllegalArgumentException("Current place not found"));

        return currentPlace.getPlaceName();
    }
}
