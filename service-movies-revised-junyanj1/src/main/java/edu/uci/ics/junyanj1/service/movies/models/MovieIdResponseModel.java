package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieIdResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movie", required = true)
    private FullMovieModel movie;

    private MovieIdResponseModel() {
    }

    private MovieIdResponseModel(int resultCode, String message, FullMovieModel movie) {
        this.resultCode = resultCode;
        this.message = message;
        this.movie = movie;
    }

    public static MovieIdResponseModel movieIdResponseModelFactory(int rc, FullMovieModel fmm) {
        ServiceLogger.LOGGER.info("Building movieID response.");
        if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new MovieIdResponseModel(-1,"Internal server error.",null);
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new MovieIdResponseModel(141,"User has insufficient privilege.",null);
        } else if (rc == 210) {
            ServiceLogger.LOGGER.info("Case 210: Found movies with search parameters.");
            return new MovieIdResponseModel(210, "Found movies with search parameters.",fmm);
        } else if (rc == 211) {
            ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
            return new MovieIdResponseModel(211,"No movies found with search parameters.",null);
        }
        return new MovieIdResponseModel();
    }
}
