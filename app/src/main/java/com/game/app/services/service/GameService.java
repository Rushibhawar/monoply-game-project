package com.game.app.services.service;

import com.game.app.dao.request.CreatePlayerRequest;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Player;

public interface GameService {
    public Game createGame(Player hostPlayer, Player player2);

}
