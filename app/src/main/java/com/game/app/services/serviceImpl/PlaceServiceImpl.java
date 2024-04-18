package com.game.app.services.serviceImpl;

import com.game.app.controller.GameController;
import com.game.app.dao.request.PlaceRequest;
import com.game.app.persistence.model.Place;
import com.game.app.persistence.repository.PlaceRepository;
import com.game.app.services.service.PlaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    Logger logger = LoggerFactory.getLogger(PlaceServiceImpl.class);


    @Override
    public Place getPlaceByPosition(int position) {
        // Assuming position is a unique identifier for places
        Optional<Place> optionalPlace = placeRepository.findByPosition(position);
        return optionalPlace.orElse(null);
    }

    @Override
    public boolean isPlaceAlreadyExists(String placeName) {

        Optional<Place> existingPlace = placeRepository.findByPlaceName(placeName);
        return existingPlace.isPresent();
    }


    @Override
    public Place addPlace(PlaceRequest request) {
        // Check if the place already exists
        Optional<Place> existingPlace = placeRepository.findByPlaceName(request.getPlaceName());
        if (existingPlace.isPresent()) {
            throw new IllegalArgumentException("Place already exists.");
        }

        int newPosition = placeRepository.countPlaces() + 1;

        // Create and save the new place
        Place newPlace = Place.builder()
                .id(UUID.randomUUID().toString())
                .placeName(request.getPlaceName())
                .buyPrice(request.getBuyPrice())
                .rentPrice(request.getRentPrice())
                .position(newPosition)
                .build();

        return placeRepository.save(newPlace);
    }

    @Override
    public void clearOwnershipOfAllPlaces() {
        // Check if there are any places with owners
        List<Place> placesWithOwners = placeRepository.findAllByOwnerIsNotNull();
        if (!placesWithOwners.isEmpty()) {
            for (Place place : placesWithOwners) {
                place.setOwner(null);
                placeRepository.save(place);
            }
        }

    }
}
