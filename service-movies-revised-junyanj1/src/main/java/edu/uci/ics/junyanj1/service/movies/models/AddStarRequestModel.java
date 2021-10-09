package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;

public class AddStarRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = true)
    private int birthYear;

    public AddStarRequestModel() {
    }

    public AddStarRequestModel(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
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

    @Override
    public String toString() {
        return "AddStarRequestModel{" +
                "name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}
