package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class AddRequestModel {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private int year;
    private String backdrop_path;
    private int budget;
    private String overview;
    private String poster_path;
    private int revenue;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] genres;

    public AddRequestModel() {
    }

    public AddRequestModel(@JsonProperty(value = "title", required = true) String title,
                           @JsonProperty(value = "director", required = true) String director,
                           @JsonProperty(value = "year", required = true) int year,
                           @JsonProperty(value = "backdrop_path") String backdrop_path,
                           @JsonProperty(value = "budget") int budget,
                           @JsonProperty(value = "overview") String overview,
                           @JsonProperty(value = "poster_path") String poster_path,
                           @JsonProperty(value = "revenue") int revenue,
                           @JsonProperty(value = "genres", required = true) GenreModel[] genres) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int getBudget() {
        return budget;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public int getRevenue() {
        return revenue;
    }

    public GenreModel[] getGenres() {
        return genres;
    }

    @Override
    public String toString() {
        return "AddRequestModel{" +
                "title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                ", backdrop_path='" + backdrop_path + '\'' +
                ", budget=" + budget +
                ", overview='" + overview + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", revenue=" + revenue +
                ", genres=" + Arrays.toString(genres) +
                '}';
    }
}
