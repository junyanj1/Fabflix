package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

public class StarsInResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private StarsInResponseModel() {
    }

    private StarsInResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static StarsInResponseModel starsInResponseModelFactory(int rc) {
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
            return new StarsInResponseModel(-3,"JSON parse exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
            return new StarsInResponseModel(-2,"JSON mapping exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new StarsInResponseModel(-1,"Internal server error.");
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new StarsInResponseModel(141,"User has insufficient privilege.");
        } else if (rc == 211) {
            ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
            return new StarsInResponseModel(211,"No movies found with search parameters.");
        } else if (rc == 230) {
            ServiceLogger.LOGGER.info("Case 230: Star successfully added to movie.");
            return new StarsInResponseModel(230,"Star successfully added to movie.");
        } else if (rc == 231) {
            ServiceLogger.LOGGER.info("Case 231: Could not add star to movie.");
            return new StarsInResponseModel(231,"Could not add star to movie.");
        } else if (rc == 232) {
            ServiceLogger.LOGGER.info("Case 232: Star already exists in movie.");
            return new StarsInResponseModel(232,"Star already exists in movie.");
        }
        return new StarsInResponseModel();
    }
}
