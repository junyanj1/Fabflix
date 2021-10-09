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
import java.io.IOException;
import java.util.ArrayList;

@Path("star")
public class StarPage {
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchForStar(@Context HttpHeaders headers,
                                  @QueryParam("name") String name,
                                  @QueryParam("birthYear") int birthYear,
                                  @QueryParam("movieTitle") String movieTitle,
                                  @QueryParam("offset") int offset,
                                  @QueryParam("limit") int limit,
                                  @QueryParam("direction") String direction,
                                  @QueryParam("orderby") String orderby
                                  ) {
        ServiceLogger.LOGGER.info("Received request for star records.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        SearchForStarRequestModel requestModel = new SearchForStarRequestModel();
        SearchForStarResponseModel responseModel;

        requestModel.setName(name);
        if (birthYear < 0) {
            ServiceLogger.LOGGER.warning("Invalid birth year input, Ignore.");
            requestModel.setBirthYear(0);
        } else {
            requestModel.setBirthYear(birthYear);
        }
        requestModel.setMovieTitle(movieTitle);
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

        if (orderby == null || (!orderby.toUpperCase().equals("NAME") && !orderby.toUpperCase().equals("BIRTHYEAR"))) {
            ServiceLogger.LOGGER.warning("No orderby. Using default.");
            requestModel.setOrderby("NAME");
        } else {
            requestModel.setOrderby(orderby);
        }

        if (direction == null || (!direction.toUpperCase().equals("DESC") && !direction.toUpperCase().equals("ASC"))) {
            ServiceLogger.LOGGER.warning("No direction. Using default.");
            requestModel.setDirection("ASC");
        } else {
            requestModel.setDirection(direction);
        }

        ServiceLogger.LOGGER.info("name: " + requestModel.getName());
        ServiceLogger.LOGGER.info("birthYear: " + requestModel.getBirthYear());
        ServiceLogger.LOGGER.info("movieTitle: " + requestModel.getMovieTitle());
        ServiceLogger.LOGGER.info("offset: " + requestModel.getOffset());
        ServiceLogger.LOGGER.info("limit: " + requestModel.getLimit());
        ServiceLogger.LOGGER.info("direction: " + requestModel.getDirection());
        ServiceLogger.LOGGER.info("orderby: " + requestModel.getOrderby());

        ArrayList<StarModel> stars = MovieRecords.searchStarsInDb(requestModel);
        if (stars == null) {
            responseModel = SearchForStarResponseModel.searchForStarResponseModelFactory(-1,null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        } else if (stars.isEmpty()) {
            responseModel = SearchForStarResponseModel.searchForStarResponseModelFactory(213,null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        } else {
            responseModel = SearchForStarResponseModel.searchForStarResponseModelFactory(212,stars);
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStarByID(@Context HttpHeaders headers,
                                    @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Search for stars by starID.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        SearchStarByIdResponseModel responseModel;

        StarModel star = MovieRecords.searchForStarsByIdInDb(id);
        if (star == null) {
            responseModel = SearchStarByIdResponseModel.searchStarByIdResponseModelFactory(-1,null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        } else if (star.getId().equals("Not found.")) {
            responseModel = SearchStarByIdResponseModel.searchStarByIdResponseModelFactory(213,null);
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        } else {
            responseModel = SearchStarByIdResponseModel.searchStarByIdResponseModelFactory(212,star);
            return Response.status(Response.Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStar(@Context HttpHeaders headers,
                            String jsonText) {
        ServiceLogger.LOGGER.info("Received request for adding star.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        ServiceLogger.LOGGER.info(jsonText);
        AddStarRequestModel requestModel;
        AddStarResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText,AddStarRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
            if (privilege_result == -1) {
                responseModel = AddStarResponseModel.addStarResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (privilege_result == 0) {
                responseModel = AddStarResponseModel.addStarResponseModelFactory(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                Boolean result = MovieRecords.insertStarToDb(requestModel);
                if (result == null) {
                    responseModel = AddStarResponseModel.addStarResponseModelFactory(221);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else if (!result) {
                    responseModel = AddStarResponseModel.addStarResponseModelFactory(222);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else {
                    responseModel = AddStarResponseModel.addStarResponseModelFactory(220);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = AddStarResponseModel.addStarResponseModelFactory(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = AddStarResponseModel.addStarResponseModelFactory(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = AddStarResponseModel.addStarResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }

    @Path("starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovies(@Context HttpHeaders headers,
                                    String jsonText) {
        ServiceLogger.LOGGER.info("Received request for adding star to movies.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("transactionID: " + transactionID);

        ServiceLogger.LOGGER.info(jsonText);
        StarsInRequestModel requestModel;
        StarsInResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText,StarsInRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            int privilege_result = MovieRecords.checkPrivilegeFromIDM(email,3);
            if (privilege_result == -1) {
                responseModel = StarsInResponseModel.starsInResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else if (privilege_result == 0) {
                responseModel = StarsInResponseModel.starsInResponseModelFactory(141);
                return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            } else {
                Boolean checkId = MovieRecords.isMovieIdInDb(requestModel.getMovieid());
                if (checkId == null) {
                    responseModel = StarsInResponseModel.starsInResponseModelFactory(-1);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else if (!checkId) {
                    responseModel = StarsInResponseModel.starsInResponseModelFactory(211);
                    return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                } else {
                    Boolean insertResult = MovieRecords.insertStarIntoMovieInDb(requestModel.getStarid(),requestModel.getMovieid());
                    if (insertResult == null) {
                        responseModel = StarsInResponseModel.starsInResponseModelFactory(231);
                        return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                    } else if (!insertResult) {
                        responseModel = StarsInResponseModel.starsInResponseModelFactory(232);
                        return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                    } else {
                        responseModel = StarsInResponseModel.starsInResponseModelFactory(230);
                        return Response.status(Response.Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = StarsInResponseModel.starsInResponseModelFactory(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = StarsInResponseModel.starsInResponseModelFactory(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).header("transactionID",transactionID).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = StarsInResponseModel.starsInResponseModelFactory(-1);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).header("transactionID",transactionID).build();
            }
        }
    }
}
