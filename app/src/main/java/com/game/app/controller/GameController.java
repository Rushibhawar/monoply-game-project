package com.game.app.controller;

import com.game.app.dao.response.APIResponse;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.GameRepository;
import com.game.app.services.service.GameService;
import com.game.app.services.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    @Autowired
    PlayerService playerService;

    @Autowired
    GameService gameService;

    @Autowired
    GameRepository gameRepository;

    Logger logger = LoggerFactory.getLogger(GameController.class);


    @PostMapping("/create-game/{hostPlayerId}/{player2Id}")
    public ResponseEntity<APIResponse<?>> createGame(
            @PathVariable String hostPlayerId,
            @PathVariable String player2Id
    ) {
        try {
            logger.info("Create Game request:");

            // Check if the host and second players exist
            Player hostPlayer = playerService.getPlayerById(hostPlayerId);

            Player player2 = playerService.getPlayerById(player2Id);

            if (hostPlayer == null || player2 == null) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Host player or second player not found"));
            }

            if (!hostPlayer.isHost()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Player do not have authority to create a game."));
            }

            // Create the game using the host player and second player
            Game newGame = gameService.createGame(hostPlayer, player2);

            if (newGame == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "An error occurred while creating the game"));
            }

            return ResponseEntity
                    .ok(new APIResponse<Game>(true, newGame, "Game created successfully"));

        } catch (Exception e) {
            logger.error("An error occurred during game creation.", e);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<String>(
                            false,
                            "",
                            "An error occurred while creating the game"));
        }
    }
}
