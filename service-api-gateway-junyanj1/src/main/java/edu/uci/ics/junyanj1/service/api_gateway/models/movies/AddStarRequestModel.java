package edu.uci.ics.junyanj1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.api_gateway.models.RequestModel;

public class AddStarRequestModel extends RequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = true)
    private int birthYear;

    @JsonCreator
    public AddStarRequestModel(@JsonProperty(value = "name", required = true) String name,
                               @JsonProperty(value = "birthYear", required = true) int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    @Override
    public String toString() {
        return "AddStarRequestModel{" +
                "name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}
