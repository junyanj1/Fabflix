package edu.uci.ics.junyanj1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckGenresByIdResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "genres")
    private ArrayList<GenreModel> genres;

    private CheckGenresByIdResponseModel() {
    }

    private CheckGenresByIdResponseModel(int resultCode, String message, ArrayList<GenreModel> genres) {
        this.resultCode = resultCode;
        this.message = message;
        this.genres = genres;
    }

    public static CheckGenresByIdResponseModel checkGenresByIdResponseModelFactory(int rc, ArrayList<GenreModel> gm) {
        if (rc == -1) {
            ServiceLogger.LOGGER.info("Case -1: Internal server error.");
            return new CheckGenresByIdResponseModel(-1,"Internal server error.",null);
        } else if (rc == 141) {
            ServiceLogger.LOGGER.info("Case 141: User has insufficient privilege.");
            return new CheckGenresByIdResponseModel(141,"User has insufficient privilege.",null);
        } else if (rc == 211) {
            ServiceLogger.LOGGER.info("Case 211: No movies found with search parameters.");
            return new CheckGenresByIdResponseModel(211,"No movies found with search parameters.",null);
        } else if (rc == 219) {
            ServiceLogger.LOGGER.info("Case 219: Genres successfully retrieved.");
            return new CheckGenresByIdResponseModel(219,"Genres successfully retrieved.",gm);
        }
        return new CheckGenresByIdResponseModel();
    }
}
