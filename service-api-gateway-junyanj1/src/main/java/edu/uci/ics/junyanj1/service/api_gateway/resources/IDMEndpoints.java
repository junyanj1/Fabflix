package edu.uci.ics.junyanj1.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.core.GatewayRecords;
import edu.uci.ics.junyanj1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.*;
import edu.uci.ics.junyanj1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.junyanj1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to register user.");
        RegisterUserRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (RegisterUserRequestModel) ModelValidator.verifyModel(jsonText, RegisterUserRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, RegisterUserResponseModel.class);
        }

        try {
            // Generate transaction id.
            String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
            ClientRequest cr = new ClientRequest();
            // get the IDM URI from IDM configs
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            // get the register endpoint path from IDM configs
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
            // set the request model
            cr.setRequest(requestModel);
            // set the transactionID
            cr.setTransactionID(transactionID);
            // set the http request method
            cr.setRequestMethod("POST");

            // Now that the ClientRequest has been built, we can add it to our queue of requests.
            GatewayService.getThreadPool().add(cr);
            ServiceLogger.LOGGER.info("Enque.");

            // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
            // requested information, and the transactionID.
//            NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUserRequest(@Context HttpHeaders headers,
                                     String jsonText) {
        ServiceLogger.LOGGER.info("Received request to login user.");
        LoginUserRequestModel requestModel;

        try {
            requestModel = (LoginUserRequestModel) ModelValidator.verifyModel(jsonText, LoginUserRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, LoginUserResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setEmail(requestModel.getEmail());
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",requestModel.getEmail()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(@Context HttpHeaders headers,
                                         String jsonText) {
        ServiceLogger.LOGGER.info("Received request to verify session.");
        VerificationSessionRequestModel requestModel;

        try {
            requestModel = (VerificationSessionRequestModel) ModelValidator.verifyModel(jsonText, VerificationSessionRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, VerificationSessionResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(requestModel.getSessionID());
            cr.setEmail(requestModel.getEmail());
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",requestModel.getEmail()).header("sessionID",requestModel.getSessionID()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(@Context HttpHeaders headers,
                                               String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to verify privilege.");
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


        PrivilegeCheckRequestModel requestModel;

        try {
            requestModel = (PrivilegeCheckRequestModel) ModelValidator.verifyModel(jsonText, PrivilegeCheckRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, PrivilegeCheckResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
            cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
            cr.setRequest(requestModel);
            cr.setTransactionID(transactionID);
            cr.setSessionID(sessionID);
            cr.setEmail(requestModel.getEmail());
            cr.setRequestMethod("POST");

            GatewayService.getThreadPool().add(cr);
            return Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionid", transactionID).header("email",requestModel.getEmail()).header("sessionID",sessionID).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST).build();
        }
    }
}
