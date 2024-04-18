package com.game.app.persistence.repository;

import com.game.app.persistence.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String> {

    Optional<Place> findByPosition(int position);
    Optional<Place> findByPlaceName(String placeName);
    @Query("SELECT COUNT(p) FROM Place p")
    int countPlaces();

    public List<Place> findAllByOwnerIsNotNull();
}
