package edu.uci.ics.junyanj1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.idm.core.UserRecords;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.idm.models.VerificationPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.VerificationPageResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("session")
public class SessionPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySession(String jsonText) {
        ServiceLogger.LOGGER.info("Received session verification request.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        VerificationPageRequestModel requestModel = null;
        VerificationPageResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, VerificationPageRequestModel.class);
            ServiceLogger.LOGGER.info("Email: " + requestModel.getEmail());
            ServiceLogger.LOGGER.info("SessionID: " + requestModel.getSessionID());

            if (requestModel.getSessionID() == null) {
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-13, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (requestModel.getSessionID().length() <= 0) {
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-13, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-10, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (!requestModel.getEmail().matches(requestModel.email_regex)) {
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-11, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (UserRecords.isUserInDb(requestModel) != 1) {
                int scenario = UserRecords.isUserInDb(requestModel);
                if (scenario == 0) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(14, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-1, null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
            else {
                String newID = UserRecords.verifySessionFromDb(requestModel);
                if (newID == null) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-1, null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                } else if (newID.equals("131")) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(131, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                } else if (newID.equals("132")) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(132, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                } else if (newID.equals("133")) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(133, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                } else if (newID.equals("134")) {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(134, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                } else {
                    responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(130, newID);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-3, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = VerificationPageResponseModel.verificationPageResponseModelFactory(-1, null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
