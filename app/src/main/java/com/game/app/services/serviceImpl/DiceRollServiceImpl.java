package com.game.app.services.serviceImpl;

import com.game.app.controller.GameController;
import com.game.app.persistence.model.DiceRoll;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Place;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.GameRepository;
import com.game.app.persistence.repository.PlaceRepository;
import com.game.app.persistence.repository.PlayerRepository;
import com.game.app.services.service.DiceRollService;
import com.game.app.services.service.PlaceService;
import com.game.app.services.service.PlayerService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class DiceRollServiceImpl implements DiceRollService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlaceService placeService;

    Logger logger = LoggerFactory.getLogger(DiceRollServiceImpl.class);


    @Transactional
    @Override
    public Player rollDice(String gameId, String playerId) {

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found"));
        Player currentPlayer = game.getCurrentPlayer();
        if (!currentPlayer.getId().equals(playerId)) {
            logger.error("It's not the turn of this player : {}, as the currentPlayer is : {}",playerId,currentPlayer);
            throw new IllegalArgumentException("It's not the turn of this player");
        }

        int dice1Value = rollDie();
        int dice2Value = rollDie();
        int totalValue = dice1Value + dice2Value; // rolls 2 dice for the player
        int newPosition = (currentPlayer.getCurrentPosition() + totalValue) %  placeRepository.countPlaces();

        logger.info("New position after rolling dice fro 2 times : {}",newPosition);

        Place newPlace = placeRepository.findByPosition(newPosition)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        currentPlayer.setCurrentPosition(newPosition);
        playerRepository.save(currentPlayer);

        if (newPlace.getOwner() == null) {
            // Auto purchase if unowned place
            if (currentPlayer.getCashBalance() >= newPlace.getBuyPrice()) {
                currentPlayer.setCashBalance(currentPlayer.getCashBalance() - newPlace.getBuyPrice());
                newPlace.setOwner(currentPlayer);
                playerRepository.save(currentPlayer);
                placeRepository.save(newPlace);
            }
            else {
                logger.error("Player does not have enough balance.");
                throw new IllegalArgumentException("Player does not have enough balance to buy the place.");
            }
        } else if (!newPlace.getOwner().getId().equals(currentPlayer.getId())) {
            // Auto pay rent if owned by another player
            int rentAmount = newPlace.getRentPrice();
            if (currentPlayer.getCashBalance() >= rentAmount) {
                currentPlayer.setCashBalance(currentPlayer.getCashBalance() - rentAmount);
                Player owner = newPlace.getOwner();
                owner.setCashBalance(owner.getCashBalance() + rentAmount);
                playerRepository.saveAll(List.of(currentPlayer, owner));
            } else {
                logger.error("Player does not have enough balance to pay rent.");
                throw new IllegalArgumentException("Player does not have enough balance to pay rent.");
            }

        } else {
            logger.info("Player already owns this place");
        }

        if (newPosition == 0) {
            logger.info("Crossing start, add $200");
            // Crossing start, add $200
            currentPlayer.setCashBalance(currentPlayer.getCashBalance() + 200);
            playerRepository.save(currentPlayer);
        }

        // Increment turn count and switch player turn
        game.setTurnCount(game.getTurnCount() + 1);
        game.setCurrentPlayer(game.getPlayer1().getId().equals(playerId) ? game.getPlayer2() : game.getPlayer1());
        Game savedGame = gameRepository.save(game);

        logger.info("Saved Game after rolling the dice: {}",savedGame);

        // Check for bankruptcy or highest cash balance winner
        Player winner = checkForBankruptcy(savedGame);

        // Check for winner at turn 50
        if (savedGame.getTurnCount() >= 50 && winner == null) {
            winner = determineWinnerAtTurn50(savedGame);
        }

        if (winner != null) {
            declareWinner(savedGame, winner);
        }

        return winner;
    }

    @Override
    public Player checkForBankruptcy(Game game) {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        // Check if player 1 is bankrupt
        if (player1.getCashBalance() <= 0) {
            logger.info("Player {} is bankrupt.", player1.getPlayerName());
            declareWinner(game, player2); // Declare player 2 as the winner
            return player2;
        }

        // Check if player 2 is bankrupt
        if (player2.getCashBalance() <= 0) {
            logger.info("Player {} is bankrupt.", player2.getPlayerName());
            declareWinner(game, player1); // Declare player 1 as the winner
            return player1;
        }

        return null; // No bankruptcy, return null
    }

    @Override
    public void declareWinner(Game game, Player winner) {
        game.setActive(false); // Mark the game as inactive
        game.setWinnerId(winner.getId()); // Set the winner ID in the game
        gameRepository.save(game); // Save the updated game state

        logger.info("Player {} is the winner!", winner.getPlayerName());

        // Clear ownership of all places
        placeService.clearOwnershipOfAllPlaces();

        // Reset cash balance for all players in this game
        resetCashBalanceForPlayers(game);
    }

    @Override
    public Player determineWinnerAtTurn50(Game game) {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        // Compare cash balances to determine the winner
        if (player1.getCashBalance() > player2.getCashBalance()) {
            return player1; // Player 1 has the highest cash balance
        } else if (player2.getCashBalance() > player1.getCashBalance()) {
            return player2; // Player 2 has the highest cash balance
        } else {
            return null; // It's a tie
        }
    }

    @Transactional
    private void resetCashBalanceForPlayers(Game game) {
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        // Reset cash balance to default value (e.g., 1000)
        player1.setCashBalance(1000);
        player2.setCashBalance(1000);

        // Save updated player data
        playerRepository.saveAll(List.of(player1, player2));

    }
    private int rollDie() {
        return (int) (Math.random() * 6) + 1; // Simulating a 6-sided die roll
    }
}
