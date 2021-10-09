package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingCartRetrieveAndClearRequestModel extends ShoppingCartRequestModel{
    @JsonCreator
    public ShoppingCartRetrieveAndClearRequestModel(
            @JsonProperty(value = "email", required = true) String email) {
        super(email);
    }
}
