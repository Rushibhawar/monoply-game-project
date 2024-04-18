package com.game.app.services.service;

import com.game.app.dao.request.PlaceRequest;
import com.game.app.persistence.model.Place;

public interface PlaceService {

    public Place getPlaceByPosition(int position);

    public boolean isPlaceAlreadyExists(String placeName);

    public Place addPlace(PlaceRequest request);
    public void clearOwnershipOfAllPlaces();


}