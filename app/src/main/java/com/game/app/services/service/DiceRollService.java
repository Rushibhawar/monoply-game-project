package com.game.app.services.service;

import com.game.app.persistence.model.DiceRoll;

public interface DiceRollService {
    public void rollDice(String gameId, String playerId);

}
