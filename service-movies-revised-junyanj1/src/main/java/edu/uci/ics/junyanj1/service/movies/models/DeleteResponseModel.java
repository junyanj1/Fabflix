package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    private DeleteResponseModel() {
    }

    private DeleteResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static DeleteResponseModel deleteResponseModelFactory(int rc) {
        if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new DeleteResponseModel(-1,"Internal server error.");
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new DeleteResponseModel(141,"User has insufficient privilege.");
        } else if (rc == 240) {
            ServiceLogger.LOGGER.info("Case 240: Movie successfully removed.");
            return new DeleteResponseModel(240,"Movie successfully removed.");
        } else if (rc == 241) {
            ServiceLogger.LOGGER.info("Case 241: Could not remove movie.");
            return new DeleteResponseModel(241,"Could not remove movie.");
        } else if (rc == 242) {
            ServiceLogger.LOGGER.info("Case 242: Movie has been already removed.");
            return new DeleteResponseModel(242,"Movie has been already removed.");
        }
        return new DeleteResponseModel();
    }
}
