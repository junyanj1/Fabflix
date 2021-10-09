package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private RatingResponseModel() {
    }

    private RatingResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static RatingResponseModel ratingResponseModelFactory(int rc) {
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
            return new RatingResponseModel(-3,"JSON parse exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
            return new RatingResponseModel(-2,"JSON mapping exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new RatingResponseModel(-1,"Internal server error.");
        } else if (rc == 211) {
            ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
            return new RatingResponseModel(211,"No movies found with search parameters.");
        } else if (rc == 250) {
            ServiceLogger.LOGGER.info("Case 250: Rating successfully updated.");
            return new RatingResponseModel(250,"Rating successfully updated.");
        } else if (rc == 251) {
            ServiceLogger.LOGGER.info("Case 251: Could not update rating.");
            return new RatingResponseModel(251,"Could not update rating.");
        }
        return new RatingResponseModel();
    }
}
