package edu.uci.ics.junyanj1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.AddRequestModel;
import edu.uci.ics.junyanj1.service.movies.models.AddResponseModel;
import edu.uci.ics.junyanj1.service.movies.models.GenreModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("add")
public class AddPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(@Context HttpHeaders headers,
                             String jsonText) {
        ServiceLogger.LOGGER.info("Receiving request to add movie.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        ServiceLogger.LOGGER.info(jsonText);
        AddRequestModel requestModel;
        AddResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText,AddRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
            if (privilege_result == -1) {
                responseModel = AddResponseModel.addResponseModelFactory(-1,null,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (privilege_result == 0) {
                responseModel = AddResponseModel.addResponseModelFactory(141,null,null);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                Boolean existed = MovieRecords.isMovieInDb(requestModel.getTitle(),requestModel.getDirector(),requestModel.getYear());
                if (existed == null) {
                    responseModel = AddResponseModel.addResponseModelFactory(-1,null,null);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else if (existed) {
                    responseModel = AddResponseModel.addResponseModelFactory(216,null,null); // Not returning movieid or genreid
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else {
                    String id = MovieRecords.insertMovieToDb(requestModel);
                    if (id.equals("error")) {
                        responseModel = AddResponseModel.addResponseModelFactory(215,null,null);
                        return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                    } else {
                        if (!MovieRecords.initiateGenresInMovies(id,requestModel.getGenres())||!MovieRecords.initiateNewMovieRatings(id))
                            ServiceLogger.LOGGER.info("Something wrong with adding new movie genres and ratings.");
                        Map<String,Integer> genreMap = MovieRecords.whatIsTheGenreListInDb();
                        int count = 0;
                        int [] genres = new int[requestModel.getGenres().length];
                        for (GenreModel g : requestModel.getGenres()) {
                            genres[count] = genreMap.get(g.getName().substring(0,1).toUpperCase()+g.getName().substring(1).toLowerCase());
                            count++;
                        }
                        responseModel = AddResponseModel.addResponseModelFactory(214,id,genres);
                        return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = AddResponseModel.addResponseModelFactory(-2,null,null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = AddResponseModel.addResponseModelFactory(-3,null,null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                e.printStackTrace();
                responseModel = AddResponseModel.addResponseModelFactory(-1,null,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }
}
