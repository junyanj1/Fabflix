package edu.uci.ics.junyanj1.service.movies.resources;

import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("get")
public class MovieIdPage {
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkMovieById(@Context HttpHeaders headers,
                                   @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request for checking movie by ID.");
        MovieIdResponseModel responseModel;

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);
        ServiceLogger.LOGGER.info("id: " + id);


        FullMovieModel movie = MovieRecords.searchForMoviesByIdInDb(email,id);
        if (movie == null) {
            responseModel = MovieIdResponseModel.movieIdResponseModelFactory(-1, null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID", transactionID).build();
        } else if (movie.getMovieId().equals("Not found.")) {
            responseModel = MovieIdResponseModel.movieIdResponseModelFactory(211, null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID", transactionID).build();
        } else if (movie.getHidden()!=null) {
            responseModel = MovieIdResponseModel.movieIdResponseModelFactory(141,null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID", transactionID).build();
        } else {
            ArrayList<GenreModel> genres;
            ArrayList<StarModel> stars;
            ServiceLogger.LOGGER.info("Checking genres of: " + movie.getTitle());
            genres = MovieRecords.searchMovieGenreInDb(movie);
            movie.setGenres(genres);
            ServiceLogger.LOGGER.info("Checking stars of: " + movie.getTitle());
            stars = MovieRecords.searchMovieStarByIdInDb(movie);
            movie.setStars(stars);

            responseModel = MovieIdResponseModel.movieIdResponseModelFactory(210,movie);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID", transactionID).build();
        }
    }
}
