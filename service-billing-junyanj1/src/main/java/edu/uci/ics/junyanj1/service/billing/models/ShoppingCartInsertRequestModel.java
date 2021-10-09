package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingCartInsertRequestModel extends ShoppingCartRequestModel {
    @JsonCreator
    public ShoppingCartInsertRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity) {
        super(email, movieId, quantity);
    }
}
