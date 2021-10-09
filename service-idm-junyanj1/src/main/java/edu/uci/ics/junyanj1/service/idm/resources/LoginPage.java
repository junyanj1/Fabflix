package edu.uci.ics.junyanj1.service.idm.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.idm.core.UserRecords;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.idm.models.LoginPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.LoginPageResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("login")
public class LoginPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received login request.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        LoginPageRequestModel requestModel = null;
        LoginPageResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, LoginPageRequestModel.class);
            ServiceLogger.LOGGER.info("Email: " + requestModel.getEmail());
            ServiceLogger.LOGGER.info("Password received.");

            if (requestModel.getPassword() == null) {
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-12, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (requestModel.getPassword().length <=0) {
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-12, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (!requestModel.getEmail().matches(requestModel.email_regex)) {
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-11, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (UserRecords.isUserInDb(requestModel) != 1) {
                int scenario = UserRecords.isUserInDb(requestModel);
                if (scenario == 0) {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(14,null);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-1, null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
            else if (UserRecords.doesPasswordMatch(requestModel) != 1) {
                int scenario = UserRecords.doesPasswordMatch(requestModel);
                if (scenario == 0) {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(11, null);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-1, null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
            else {
                String id = UserRecords.createNewLoginSessionToDb(requestModel);
                if (!id.equals(null)) {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(120, id);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else {
                    responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-1, null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-2,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-3, null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = LoginPageResponseModel.LoginPageResponseModelFactory(-1, null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

}
