package com.game.app.services.service;

import com.game.app.dao.request.CreatePlayerRequest;
import com.game.app.persistence.model.Player;

public interface PlayerService {

    public Player createPlayer(CreatePlayerRequest request);

    public String isPlayerDataValid(String email, String name);

    public Player getPlayerById(String playerId);

    public Player savePlayer(Player player);
}