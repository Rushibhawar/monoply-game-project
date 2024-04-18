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


//    @Override
//    public DiceRoll rollDice(String playerId) {
//        // Generate random dice values (1-6 for each die)
//        int dice1Value = rollDie();
//        int dice2Value = rollDie();
//
//        int totalValue = dice1Value + dice2Value;
//
//        // Update player's current position based on the dice value
//        Player currentPlayer = playerService.getPlayerById(playerId);
//        int currentPosition = currentPlayer.getCurrentPosition();
//        int newPosition = (currentPosition + totalValue) % 10; // Assuming 10 places in the game
//
//        // Check if the new position is an unowned place or owned by another player
//        Place newPlace = placeService.getPlaceByPosition(newPosition);
//        if (newPlace.getOwner() == null) {
//            // Unowned place
//            currentPlayer.setCurrentPosition(newPosition);
//            playerService.savePlayer(currentPlayer);
//        } else {
//            // Place owned by another player
//            // Implement logic for paying rent or other actions
//        }
//
//        // Save the dice roll details
//        DiceRoll diceRoll = new DiceRoll();
//        diceRoll.setPlayer(currentPlayer);
//        diceRoll.setDice1Value(dice1Value);
//        diceRoll.setDice2Value(dice2Value);
//        diceRoll.setTotalValue(totalValue);
//        // Save diceRoll to database using DiceRollRepository or JPA EntityManager
//
//        return diceRoll;
//    }
//
//    private int rollDie() {
//        Random random = new Random();
//        return random.nextInt(6) + 1; // Generate random value between 1 and 6
//    }


    @Transactional
    @Override
    public void rollDice(String gameId, String playerId) {

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
    }

    private int rollDie() {
        return (int) (Math.random() * 6) + 1; // Simulating a 6-sided die roll
    }
}
