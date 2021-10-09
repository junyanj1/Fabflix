package edu.uci.ics.junyanj1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.AddStarRequestModel;
import edu.uci.ics.junyanj1.service.movies.models.RatingModel;
import edu.uci.ics.junyanj1.service.movies.models.RatingRequestModel;
import edu.uci.ics.junyanj1.service.movies.models.RatingResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("rating")
public class RatingPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response ratingUpdate(@Context HttpHeaders headers,
                                 String jsonText) {
        ServiceLogger.LOGGER.info("Receiving the request for updating rating score.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        ServiceLogger.LOGGER.info(jsonText);
        RatingRequestModel requestModel;
        RatingResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, RatingRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            Boolean checkId = MovieRecords.isMovieIdInDb(requestModel.getId());
            if (checkId == null) {
                responseModel = RatingResponseModel.ratingResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (!checkId) {
                responseModel = RatingResponseModel.ratingResponseModelFactory(211);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                if (requestModel.getRating()<0.0 || requestModel.getRating()>10.0) {
                    responseModel = RatingResponseModel.ratingResponseModelFactory(251);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                }
                Boolean updateResult = MovieRecords.updateRatingInDb(requestModel.getId(),requestModel.getRating());
                if (updateResult == null) {
                    responseModel = RatingResponseModel.ratingResponseModelFactory(-1);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else if (!updateResult) {
                    responseModel = RatingResponseModel.ratingResponseModelFactory(251);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else {
                    responseModel = RatingResponseModel.ratingResponseModelFactory(250);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = RatingResponseModel.ratingResponseModelFactory(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = RatingResponseModel.ratingResponseModelFactory(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = RatingResponseModel.ratingResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }
}
