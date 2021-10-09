package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchForStarResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "stars", required = true)
    private ArrayList<StarModel> stars;

    private SearchForStarResponseModel() {
    }

    private SearchForStarResponseModel(int resultCode, String message, ArrayList<StarModel> stars) {
        this.resultCode = resultCode;
        this.message = message;
        this.stars = stars;
    }

    public static SearchForStarResponseModel searchForStarResponseModelFactory(int rc, ArrayList<StarModel> sm) {
        if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new SearchForStarResponseModel(-1,"Internal server error.",null);
        } else if (rc == 212) {
            ServiceLogger.LOGGER.info("Case 212: Found stars with search parameters.");
            return new SearchForStarResponseModel(212,"Found stars with search parameters.",sm);
        } else if (rc == 213) {
            ServiceLogger.LOGGER.info("Case 213: No stars found with search parameters.");
            return new SearchForStarResponseModel(213,"No stars found with search parameters.",null);
        }
        return new SearchForStarResponseModel();
    }
}
