package edu.uci.ics.junyanj1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartRequestModel {
    private String email;
    private String movieId;
    private int quantity;

    public ShoppingCartRequestModel() {}

    public ShoppingCartRequestModel(String email) {
        this.email = email;
    }

    public ShoppingCartRequestModel(String email, String movieId) {
        this.email = email;
        this.movieId = movieId;
    }

    public ShoppingCartRequestModel(String email, String movieId, int quantity) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ShoppingCartRequestModel{" +
                "email='" + email + '\'' +
                ", movieId='" + movieId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getQuantity() {
        return quantity;
    }
}
