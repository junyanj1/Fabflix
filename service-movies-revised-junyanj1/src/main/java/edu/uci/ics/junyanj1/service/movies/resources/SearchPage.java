package edu.uci.ics.junyanj1.service.movies.resources;

import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.MovieModel;
import edu.uci.ics.junyanj1.service.movies.models.SearchRequestModel;
import edu.uci.ics.junyanj1.service.movies.models.SearchResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchRequest(@Context HttpHeaders headers,
                                  @QueryParam("title") String title,
                                  @QueryParam("genre") String genre,
                                  @QueryParam("year") int year,
                                  @QueryParam("director") String director,
                                  @QueryParam("hidden") boolean hidden,
                                  @QueryParam("offset") int offset,
                                  @QueryParam("limit") int limit,
                                  @QueryParam("orderby") String direction,
                                  @QueryParam("direction") String orderby
    ) {
        try {
            ServiceLogger.LOGGER.info("Received request for movie records.");
            SearchRequestModel requestModel = new SearchRequestModel();
            SearchResponseModel responseModel;

            requestModel.setTitle(title);
            requestModel.setGenre(genre);
            if (year < 0) {
                ServiceLogger.LOGGER.warning("Invalid year input, Ignore.");
                requestModel.setYear(0);
            } else {
                requestModel.setYear(year);
            }
            requestModel.setDirector(director);
            requestModel.setHidden(hidden);
            if (limit == 10 || limit == 25 || limit == 50 || limit == 100) {
                requestModel.setLimit(limit);
            } else {
                ServiceLogger.LOGGER.warning("Invalid limit. Using default.");
                limit = 10;
                requestModel.setLimit(10);
            }

            if (offset >= 0 && offset % limit == 0) {
                requestModel.setOffset(offset);
            } else {
                ServiceLogger.LOGGER.warning("Invalid offset. Using default.");
                requestModel.setOffset(0);
            }

            if (direction == null || (!direction.toUpperCase().equals("RATING") && !direction.toUpperCase().equals("TITLE"))) {
                ServiceLogger.LOGGER.warning("No direction. Using default.");
                requestModel.setDirection("RATING");
            } else {
                requestModel.setDirection(direction);
            }

            if (orderby == null || (!orderby.toUpperCase().equals("DESC") && !orderby.toUpperCase().equals("ASC"))) {
                ServiceLogger.LOGGER.warning("No orderby. Using default.");
                requestModel.setOrderby("DESC");
            } else {
                requestModel.setOrderby(orderby);
            }

            ServiceLogger.LOGGER.info("title: " + requestModel.getTitle());
            ServiceLogger.LOGGER.info("genre: " + requestModel.getGenre());
            ServiceLogger.LOGGER.info("year: " + requestModel.getYear());
            ServiceLogger.LOGGER.info("director: " + requestModel.getDirector());
            ServiceLogger.LOGGER.info("hidden: " + requestModel.isHidden());
            ServiceLogger.LOGGER.info("offset: " + requestModel.getOffset());
            ServiceLogger.LOGGER.info("limit: " + requestModel.getLimit());
            ServiceLogger.LOGGER.info("orderby: " + requestModel.getOrderby());
            ServiceLogger.LOGGER.info("direction: " + requestModel.getDirection());

            // Get email and sessionID from the HTTP header
            String email = headers.getHeaderString("email");
            String sessionID = headers.getHeaderString("sessionID");
            String transactionID = headers.getHeaderString("transactionID");
            ServiceLogger.LOGGER.info("email: " + email);
            ServiceLogger.LOGGER.info("sessionID: " + sessionID);
            ServiceLogger.LOGGER.info("transactionID: " + transactionID);

            ArrayList<MovieModel> movies = MovieRecords.searchForMoviesFromDb(requestModel, email);
            if (movies == null) {
                responseModel = SearchResponseModel.SearchResponseModelFactory(-1, null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            } else if (movies.isEmpty()) {
                responseModel = SearchResponseModel.SearchResponseModelFactory(211, null);
                return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            } else {
                responseModel = SearchResponseModel.SearchResponseModelFactory(210, movies);
                return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
