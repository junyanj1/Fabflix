package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchStarByIdResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "star", required = true)
    private StarModel star;

    private SearchStarByIdResponseModel() {
    }

    private SearchStarByIdResponseModel(int resultCode, String message, StarModel star) {
        this.resultCode = resultCode;
        this.message = message;
        this.star = star;
    }

    public static SearchStarByIdResponseModel searchStarByIdResponseModelFactory(int rc, StarModel sm) {
        if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new SearchStarByIdResponseModel(-1,"Internal server error.",null);
        } else if (rc == 212) {
            ServiceLogger.LOGGER.info("Case 212: Found stars with search parameters.");
            return new SearchStarByIdResponseModel(212,"Found stars with search parameters.",sm);
        } else if (rc == 213) {
            ServiceLogger.LOGGER.info("Case 213: No stars found with search parameters.");
            return new SearchStarByIdResponseModel(213,"No stars found with search parameters.",null);
        }
        return new SearchStarByIdResponseModel();
    }
}
