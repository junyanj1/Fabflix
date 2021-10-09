package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

public class AddStarResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private AddStarResponseModel() {
    }

    private AddStarResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static AddStarResponseModel addStarResponseModelFactory(int rc) {
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
            return new AddStarResponseModel(-3,"JSON parse exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
            return new AddStarResponseModel(-2,"JSON mapping exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new AddStarResponseModel(-1,"Internal server error.");
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new AddStarResponseModel(141,"User has insufficient privilege.");
        } else if (rc == 220) {
            ServiceLogger.LOGGER.info("Case 220: Star successfully added.");
            return new AddStarResponseModel(220,"Star successfully added.");
        } else if (rc == 221) {
            ServiceLogger.LOGGER.info("Case 221: Could not add star.");
            return new AddStarResponseModel(221,"Could not add star.");
        } else if (rc == 222) {
            ServiceLogger.LOGGER.info("Case 222: Star already exists.");
            return new AddStarResponseModel(222,"Star already exists.");
        }
        return new AddStarResponseModel();
    }
}
