package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "movieid")
    private String movieid;
    @JsonProperty(value = "genreid")
    private int[] genreid;

    private AddResponseModel() {
    }

    private AddResponseModel(int resultCode, String message, String movieid, int[] genreid) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = movieid;
        this.genreid = genreid;
    }

    public static AddResponseModel addResponseModelFactory(int rc,String movieid,int[] genreid) {
        if (rc == -3) {
            ServiceLogger.LOGGER.info("Case -3: JSON parse exception.");
            return new AddResponseModel(-3,"JSON parse exception.",null,null);
        } else if (rc == -2) {
            ServiceLogger.LOGGER.info("Case -2: JSON mapping exception.");
            return new AddResponseModel(-2,"JSON mapping exception.",null,null);
        } else if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new AddResponseModel(-1,"Internal server error.",null,null);
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new AddResponseModel(141,"User has insufficient privilege.",null,null);
        } else if (rc == 214) {
            ServiceLogger.LOGGER.info("Case 214: Movie successfully added.");
            return new AddResponseModel(214,"Movie successfully added.",movieid,genreid);
        } else if (rc == 215) {
            ServiceLogger.LOGGER.info("Case 215: Could not add movie.");
            return new AddResponseModel(215,"Could not add movie.",null,null);
        } else if (rc == 216) {
            ServiceLogger.LOGGER.info("Case 216: Movie already exists.");
            return new AddResponseModel(216, "Movie already exists.",movieid,genreid);
        }
        return new AddResponseModel();
    }
}
