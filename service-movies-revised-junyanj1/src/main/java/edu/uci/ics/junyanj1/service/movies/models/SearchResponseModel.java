package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movies", required = true)
    private ArrayList<MovieModel> movies;

    private SearchResponseModel() {
    }

    private SearchResponseModel(int resultCode, String message, ArrayList<MovieModel> movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;
    }

    public static SearchResponseModel SearchResponseModelFactory(int rc, ArrayList<MovieModel> ms) {
        if (rc == -1) {
            ServiceLogger.LOGGER.warning("Case -1: Internal server error.");
            return new SearchResponseModel(-1,"Internal server error.",null);
        } else if (rc == 210) {
            ServiceLogger.LOGGER.info("Case 210: Found movies with search parameters.");
            return new SearchResponseModel(210, "Found movies with search parameters.", ms);
        } else if (rc == 211) {
            ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
            return new SearchResponseModel(211,"No movies found with search parameters.", null);
        }
        return new SearchResponseModel();
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<MovieModel> getMovies() {
        return movies;
    }
}
