package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

public class AddGenreResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private AddGenreResponseModel() {
    }

    private AddGenreResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static AddGenreResponseModel addGenreResponseModelFactory(int rc) {
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
            return new AddGenreResponseModel(-3,"JSON parse exception.");
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
            return new AddGenreResponseModel(-2,"JSON mapping exception.");
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new AddGenreResponseModel(-1,"Internal server error.");
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new AddGenreResponseModel(141,"User has insufficient privilege.");
        } else if (rc == 217) {
            ServiceLogger.LOGGER.info("Case 217: Genre successfully added.");
            return new AddGenreResponseModel(217,"Genre successfully added.");
        } else if (rc == 218) {
            ServiceLogger.LOGGER.info("Case 218: Genre could not be added.");
            return new AddGenreResponseModel(218,"Genre could not be added.");
        }
        return new AddGenreResponseModel();
    }
}
