package edu.uci.ics.junyanj1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.movies.core.MovieRecords;
import edu.uci.ics.junyanj1.service.movies.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.movies.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;

@Path("genre")
public class GenrePage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenres(@Context HttpHeaders headers) {
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        GetGenreResponseModel responseModel;

        int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
        // Stop checking privilege for Frontend
        privilege_result = 1;
        if (privilege_result == -1) {
            responseModel = GetGenreResponseModel.getGenreResponseModelFactory(-1,null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else if (privilege_result == 0) {
            responseModel = GetGenreResponseModel.getGenreResponseModelFactory(141,null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else {
            ArrayList<GenreModel> genres = MovieRecords.getGenresFromDb();
            if (genres.isEmpty()) {
                responseModel = GetGenreResponseModel.getGenreResponseModelFactory(219,null);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                responseModel = GetGenreResponseModel.getGenreResponseModelFactory(219, genres);
                return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }
        }
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenre(@Context HttpHeaders headers,
                             String jsonText) {
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        ServiceLogger.LOGGER.info(jsonText);
        AddGenreRequestModel requestModel;
        AddGenreResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText,AddGenreRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
            if (privilege_result == -1) {
                responseModel = AddGenreResponseModel.addGenreResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (privilege_result == 0) {
                responseModel = AddGenreResponseModel.addGenreResponseModelFactory(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                Boolean check = MovieRecords.addGenreToDb(requestModel.getName());
                if (check == null) {
                    responseModel = AddGenreResponseModel.addGenreResponseModelFactory(-1);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else if (!check) {
                    responseModel = AddGenreResponseModel.addGenreResponseModelFactory(218);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else {
                    responseModel = AddGenreResponseModel.addGenreResponseModelFactory(217);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = AddGenreResponseModel.addGenreResponseModelFactory(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = AddGenreResponseModel.addGenreResponseModelFactory(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                e.printStackTrace();
                responseModel = AddGenreResponseModel.addGenreResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkGenresById(@Context HttpHeaders headers,
                                   @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request for checking genre by movieID.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);
        ServiceLogger.LOGGER.info("id: " + id);

        CheckGenresByIdResponseModel responseModel;

        int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
        if (privilege_result == -1) {
            responseModel = CheckGenresByIdResponseModel.checkGenresByIdResponseModelFactory(-1,null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else if (privilege_result == 0) {
            responseModel = CheckGenresByIdResponseModel.checkGenresByIdResponseModelFactory(141,null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
        } else {
            ArrayList<GenreModel> genres = MovieRecords.checkGenresByIdInDb(id);
            if (genres == null) {
                responseModel = CheckGenresByIdResponseModel.checkGenresByIdResponseModelFactory(-1,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (genres.isEmpty()) {
                responseModel = CheckGenresByIdResponseModel.checkGenresByIdResponseModelFactory(211,null);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                responseModel = CheckGenresByIdResponseModel.checkGenresByIdResponseModelFactory(219,genres);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }
}
