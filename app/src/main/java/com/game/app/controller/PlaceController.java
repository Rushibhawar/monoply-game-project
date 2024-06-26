package com.game.app.controller;

import com.game.app.dao.request.PlaceRequest;
import com.game.app.dao.response.APIResponse;
import com.game.app.persistence.model.Game;
import com.game.app.persistence.model.Place;
import com.game.app.persistence.model.Player;
import com.game.app.services.service.PlaceService;
import com.game.app.services.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor

public class PlaceController {

    @Autowired
    PlaceService placeService;

    @Autowired
    PlayerService playerService;

    Logger logger = LoggerFactory.getLogger(PlaceController.class);

    @PostMapping("/add-place/{playerId}")
    public ResponseEntity<APIResponse<?>> addPlace(@RequestBody PlaceRequest placeRequest, @PathVariable String playerId) {
        try {
            // Validate place request
            if (placeRequest == null || placeRequest.getPlaceName() == null || placeRequest.getPlaceName().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Invalid place data."));
            }

            Player player = playerService.getPlayerById(playerId);

            if (!player.isHost()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Player do not have authority to add place."));
            }

            // Check if the place already exists
//            Optional<Place> existingPlace = placeRepository.findByPlaceName(placeRequest.getPlaceName());
//            if (existingPlace.isPresent()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Place already exists.");
//            }

            // Check if the place already exists
            if(placeService.isPlaceAlreadyExists(placeRequest.getPlaceName())) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<String>(
                                false,
                                "",
                                "Place already exists."));
            }

            // Create and save the new place
//            Place newPlace = new Place();
//            newPlace.setPlaceName(placeRequest.getPlaceName());
//            newPlace.setBuyPrice(placeRequest.getBuyPrice());
//            newPlace.setRentPrice(placeRequest.getRentPrice());
//            placeRepository.save(newPlace);
            Place addedPlace = placeService.addPlace(placeRequest);

            return ResponseEntity
                    .ok(new APIResponse<Place>(true, addedPlace, "Place added successfully."));

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
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<String>(
                            false,
                            "",
                            "Error adding place."));
        }
    }

}
