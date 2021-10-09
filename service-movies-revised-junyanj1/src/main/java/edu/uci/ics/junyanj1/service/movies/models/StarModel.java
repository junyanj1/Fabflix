package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StarModel {
    private String id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int birthYear;

    public StarModel() {}

    @JsonCreator
    public StarModel(@JsonProperty(value = "id", required = true) String id,
                     @JsonProperty(value = "name", required = true) String name
    ) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    public StarModel(@JsonProperty(value = "id", required = true) String id,
                     @JsonProperty(value = "name", required = true) String name,
                     @JsonProperty(value = "birthYear") int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }
}
