package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddGenreRequestModel {
    @JsonProperty(value = "name")
    private String name;

    public AddGenreRequestModel() {
    }

    public AddGenreRequestModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AddGenreRequestModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
