package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingCartDeleteRequestModel extends ShoppingCartRequestModel {
    @JsonCreator
    public ShoppingCartDeleteRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId) {
        super(email, movieId);
    }
}
