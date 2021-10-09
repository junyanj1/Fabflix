package edu.uci.ics.junyanj1.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.core.GatewayRecords;
import edu.uci.ics.junyanj1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.IDMSessionVerificationParseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.movies.*;
import edu.uci.ics.junyanj1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to find movie.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());
        cr.setQueryParams(uriInfo.getQueryParameters());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
    }

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers,
                                    @PathParam("movieid") String movieid) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to get movie by id.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("movieid: " + movieid);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieGet().replaceAll("\\{.*\\}",movieid));
        ServiceLogger.LOGGER.info("endpoint from movieid get endpoint: "+ cr.getEndpoint());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();

    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers,
                                    String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to add movie.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        AddMovieRequestModel requestModel;
        try {
            requestModel = (AddMovieRequestModel) ModelValidator.verifyModel(jsonText, AddMovieRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, AddMovieResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(email);
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers,
                                       @PathParam("movieid") String movieid) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to delete movie by id.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("movieid: " + movieid);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieDelete().replaceAll("\\{.*\\}",movieid));
        ServiceLogger.LOGGER.info("endpoint from movieid get endpoint: "+ cr.getEndpoint());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("DELETE");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
    }

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve all genres.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);

        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

//        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);
//
//        String jt = response.readEntity(String.class);
//        ServiceLogger.LOGGER.info("jsonText: " + jt);
//        IDMSessionVerificationParseModel getModel;
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
//            if (getModel.getResultCode()==130) {
//                // active
//                if (getModel.getSessionID()!=null)
//                    sessionID = getModel.getSessionID();
//            } else {
//                return Response.status(response.getStatus()).entity(jt).build();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            if (e instanceof JsonMappingException) {
//                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
//            }
//            else if (e instanceof JsonParseException) {
//                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
//            }
//            else {
//                ServiceLogger.LOGGER.warning("Other IOException.");
//            }
//        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();

    }

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers,
                                    String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to add genre.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        AddGenreRequestModel requestModel;
        try {
            requestModel = (AddGenreRequestModel) ModelValidator.verifyModel(jsonText, AddGenreRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, AddGenreResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(email);
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers,
                                             @PathParam("movieid") String movieid) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve all genres.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("movieid: " + movieid);

        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreMovie().replaceAll("\\{.*\\}",movieid));
        ServiceLogger.LOGGER.info("endpoint from movieid get endpoint: "+ cr.getEndpoint());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();

    }

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers,
                                      @Context UriInfo uriInfo) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to find star.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());
        cr.setQueryParams(uriInfo.getQueryParameters());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();

    }

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers,
                                   @PathParam("id") String id) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve star by id.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("starid: " + id);

        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        String transactionID = TransactionIDGenerator.generateTransactionID();
        ClientRequest cr = new ClientRequest();
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarGet().replaceAll("\\{.*\\}",id));
        ServiceLogger.LOGGER.info("endpoint from movieid get endpoint: "+ cr.getEndpoint());
        cr.setTransactionID(transactionID);
        cr.setSessionID(sessionID);
        cr.setEmail(email);
        cr.setRequestMethod("GET");

        GatewayService.getThreadPool().add(cr);
        return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();

    }

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers,
                                   String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to add star.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        AddStarRequestModel requestModel;
        try {
            requestModel = (AddStarRequestModel) ModelValidator.verifyModel(jsonText, AddStarRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, AddStarResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(email);
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers,
                                          String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to add star to a movie.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        StarsInRequestModel requestModel;
        try {
            requestModel = (StarsInRequestModel) ModelValidator.verifyModel(jsonText, StarsInRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, StarsInResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(email);
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers,
                                        String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to add a rating to movie.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        if (email == null) {
            verifyResponse = new VerifySessionResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
        if (sessionID == null) {
            verifyResponse = new VerifySessionResponseModel(-17);
            return Response.status(Status.BAD_REQUEST).entity(verifyResponse).build();
        }

        Response response = GatewayRecords.checkSessionFromIDM(email,sessionID);

        String jt = response.readEntity(String.class);
        ServiceLogger.LOGGER.info("jsonText: " + jt);
        IDMSessionVerificationParseModel getModel;

        try {
            ObjectMapper mapper = new ObjectMapper();
            getModel = mapper.readValue(jt, IDMSessionVerificationParseModel.class);
            if (getModel.getResultCode()==130) {
                // active
                if (getModel.getSessionID()!=null)
                    sessionID = getModel.getSessionID();
            } else {
                return Response.status(response.getStatus()).entity(jt).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
            }
        }

        RatingRequestModel requestModel;
        try {
            requestModel = (RatingRequestModel) ModelValidator.verifyModel(jsonText, RatingRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, RatingResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
            cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(email);
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",email).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }
}
