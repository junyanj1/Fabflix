package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class FullMovieModel {
    @JsonProperty(required = true)
    private String movieId;
    @JsonProperty(required = true)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String director;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int year;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String backdrop_path;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int budget;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String overview;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String poster_path;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int revenue;
    @JsonProperty(required = true)
    private float rating;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int numVotes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hidden = null;

    // Genres and stars
    @JsonProperty(required = true)
    ArrayList<GenreModel> genres;
    @JsonProperty(required = true)
    ArrayList<StarModel> stars;

    public FullMovieModel() {}

    @JsonCreator
    public FullMovieModel(@JsonProperty(required = true) String movieId,
                      @JsonProperty(required = true) String title,
                      @JsonProperty(required = true) String director,
                      @JsonProperty(required = true) int year, String backdrop_path, int budget, String overview, String poster_path, int revenue,
                          float rating, int numVotes, Boolean hidden) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = hidden;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public ArrayList<GenreModel> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<GenreModel> genres) {
        this.genres = genres;
    }

    public ArrayList<StarModel> getStars() {
        return stars;
    }

    public void setStars(ArrayList<StarModel> stars) {
        this.stars = stars;
    }
}
