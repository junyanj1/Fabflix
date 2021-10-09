package edu.uci.ics.junyanj1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class ShoppingCartDeleteRequestModel extends RequestModel {
    @JsonProperty(value = "email",required = true)
    private String email;
    @JsonProperty(value = "movieId",required = true)
    private String movieId;

    @JsonCreator
    public ShoppingCartDeleteRequestModel(
            @JsonProperty(value = "email",required = true) String email,
            @JsonProperty(value = "movieId",required = true) String movieId) {
        this.email = email;
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return "ShoppingCartDeleteRequestModel{" +
                "email='" + email + '\'' +
                ", movieId='" + movieId + '\'' +
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
}
