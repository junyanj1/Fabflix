package edu.uci.ics.junyanj1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreModel {
    private int id;
    private String name;

    public GenreModel() {}

    @JsonCreator
    public GenreModel(@JsonProperty(value = "id") int id,
                      @JsonProperty(value = "name") String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GenreModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
