package com.game.app.controller;

import com.game.app.dao.request.*;
import com.game.app.dao.response.*;
import com.game.app.persistence.model.Player;
import com.game.app.persistence.repository.PlayerRepository;
import com.game.app.services.service.PlayerService;
import com.game.app.services.serviceImpl.PlayerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

    @Autowired
    PlayerService playerService;

    @Autowired
    PlayerRepository playerRepository;

    Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);


    @PostMapping("/create-player")
    public ResponseEntity<?> createPlayer(@RequestBody CreatePlayerRequest request) {
        try {

            logger.info("Create Player request: {}", request);

            String check = playerService.isPlayerDataValid(request.getPlayerEmail(), request.getPlayerName());
            if (check != null)
                return new ResponseEntity<>(check, HttpStatus.BAD_REQUEST);

            if (playerRepository.existsByPlayerEmail(request.getPlayerEmail())) {
                logger.error("Email already exists");
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Player already exists with that email"));
            }

            Player createdPlayer = playerService.createPlayer(request);

            if (createdPlayer == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new APIResponse<String>(
                                        false,
                                        "",
                                        "Some error occurred while creating player"));
            }
            return ResponseEntity
                    .ok(new APIResponse<Player>(true, createdPlayer, "Player created successfully."));


        } catch (Exception e) {
            logger.error("An error occurred during creation.", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<String>(
                            false,
                            "",
                            "Some error occurred while creating player"));
        }
    }



}
