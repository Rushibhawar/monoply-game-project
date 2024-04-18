package com.game.app.controller;

import com.game.app.dao.response.APIResponse;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.GameRepository;
import com.game.app.services.service.DiceRollService;
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

    @Autowired
    DiceRollService diceRollService;

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
                                hostPlayer == null && player2 == null ?
                                        "Host player and second player not found" :
                                        (hostPlayer == null ? "Host" : "Second") + " player not found"));
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


    @PostMapping("/roll-dice/{gameId}/{playerId}")
    public ResponseEntity<APIResponse<?>> rollDice(@PathVariable String gameId, @PathVariable String playerId) {
        try {
            // Validate gameId and playerId
            if (gameId == null || gameId.isEmpty() || playerId == null || playerId.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Invalid gameId or playerId."));
            }

            // Check if the game exists
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Game not found."));
            }

            // Check if the game is active
            if (!game.isActive() && game.getWinnerId().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Game is not active."));
            }

            // Check if the game is already completed and won by a player
            if (!game.isActive() && !game.getWinnerId().isEmpty()) {
                Player winner = playerService.getWinnerDetailsById(game.getWinnerId());
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new APIResponse<String>(
                                    false,
                                    "",
                                    "Game is already completed and won by " + ( winner != null ? winner.getPlayerName() : "N/A")  + "."));
            }

            // Check if it's the turn of the current player
            Player currentPlayer = game.getCurrentPlayer();
            if (!currentPlayer.getId().equals(playerId)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "It's not the turn of this player."));
            }

            // Roll dice and update game state
//            diceRollService.rollDice(gameId, playerId);

            // Roll dice and update game state, get the winner
            Player winner = diceRollService.rollDice(gameId, playerId);

            // Get the place landed after rolling dice
            String placeLanded = gameService.getPlaceLanded(gameId, playerId);

            // Construct response message including place landed information
            String currentlyPlayingPlayer = (game.getPlayer1().getId().equals(playerId)) ?
                                    game.getPlayer1().getPlayerName() : game.getPlayer2().getPlayerName();
            String responseMessage ="Dice rolled successfully. Player : " + currentlyPlayingPlayer + " landed on: " + placeLanded;

            // Update API response based on winner
            if (winner != null) {
                responseMessage += " The game has ended. " + winner.getPlayerName() + " is the winner!";
                return ResponseEntity
                        .ok(new APIResponse<Player>(true, winner, responseMessage));
            }
//            else {
//                responseMessage += "\nIt's a tie at turn 50!";
//            }

            return ResponseEntity
                    .ok(new APIResponse<Game>(true, null, responseMessage));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<String>(
                            false,
                            "",
                            e.getMessage()));
        } catch (Exception e) {
            logger.error("error:{}",e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<String>(
                            false,
                            "",
                            "Error rolling dice."));
        }
    }

}
