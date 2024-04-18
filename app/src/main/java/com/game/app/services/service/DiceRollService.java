package com.game.app.services.service;

import com.game.app.persistence.model.DiceRoll;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Player;

public interface DiceRollService {
    public Player rollDice(String gameId, String playerId);
    public Player checkForBankruptcy(Game game);
    public void declareWinner(Game game, Player winner);
    public Player determineWinnerAtTurn50(Game game);
}
