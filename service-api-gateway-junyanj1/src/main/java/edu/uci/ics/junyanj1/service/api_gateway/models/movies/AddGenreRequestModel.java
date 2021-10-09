package edu.uci.ics.junyanj1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class AddGenreRequestModel extends RequestModel {
    @JsonProperty(value = "name")
    private String name;

    @JsonCreator
    public AddGenreRequestModel(@JsonProperty(value = "name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "AddGenreRequestModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
