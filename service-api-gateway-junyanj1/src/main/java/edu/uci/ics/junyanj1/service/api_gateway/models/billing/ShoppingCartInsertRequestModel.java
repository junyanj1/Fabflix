package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class ShoppingCartInsertRequestModel extends RequestModel {
    @JsonProperty(value = "email",required = true)
    private String email;
    @JsonProperty(value = "movieId",required = true)
    private String movieId;
    @JsonProperty(value = "quantity",required = true)
    private int quantity;

    @JsonCreator
    public ShoppingCartInsertRequestModel(
            @JsonProperty(value = "email",required = true) String email,
            @JsonProperty(value = "movieId",required = true) String movieId,
            @JsonProperty(value = "quantity",required = true) int quantity) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ShoppingCartInsertRequestModel{" +
                "email='" + email + '\'' +
                ", movieId='" + movieId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @JsonProperty(value = "email",required = true)
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "movieId",required = true)
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty(value = "quantity",required = true)
    public int getQuantity() {
        return quantity;
    }
}
