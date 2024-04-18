package com.game.app.services.serviceImpl;

import com.game.app.dao.request.CreatePlayerRequest;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.PlayerRepository;
import com.game.app.services.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Override
    public Player createPlayer(CreatePlayerRequest request) {

        if (request == null) {
            return null;
        }

        Player player = Player.builder()
                .id(UUID.randomUUID().toString())
                .playerName(request.getPlayerName())
                .playerEmail(request.getPlayerEmail())
                .cashBalance(1000)
                .currentPosition(0)
                .isHost(request.isHost())
                .build();

        Player savedPlayer = playerRepository.save(player);
        logger.info("Saved Player : {}", savedPlayer);
        return savedPlayer;
    }

    @Override
    public String isPlayerDataValid(String email, String name) {
        if (email == null  || email.length() < 3
                || name == null || name.isEmpty())
            return "Required fields are missing!";

        return null;
    }

    @Override
    public Player getPlayerById(String playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    public String isUserDataValid(String email, String password, String fname, String lname) {
        if (email == null  || email.length() < 3
                || password == null || password.length() == 0
                || fname == null || fname.length() == 0
                || lname == null || lname.length() == 0)
            return "Required fields are missing!";

        if (password.length() > 128)
            return "Error processing password. Please try again later or contact our support.";

        if (email.length() > 63)
            return "Email is too long (max 63 characters).";

        return null;
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player getWinnerDetailsById(String winnerId) {
        return playerRepository.findById(winnerId).orElse(null);

    }
}
