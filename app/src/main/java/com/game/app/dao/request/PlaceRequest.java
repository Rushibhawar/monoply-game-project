package com.game.app.dao.request;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PlaceRequest {

    private String placeName;
    private int buyPrice;
    private int rentPrice;

}
