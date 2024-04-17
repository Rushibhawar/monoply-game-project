package com.game.app.dao.request;

import com.game.app.persistence.model.Game;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreatePlayerRequest {

    private String playerName;

    private String playerEmail;

    private boolean isHost;
}
