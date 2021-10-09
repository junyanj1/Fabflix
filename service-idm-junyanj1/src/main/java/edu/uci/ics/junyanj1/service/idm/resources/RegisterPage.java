package edu.uci.ics.junyanj1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.idm.core.UserRecords;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.idm.models.RegisterPageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.RegisterPageResponseModel;

import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.ws.Service;
import java.io.IOException;

@Path("register")
public class RegisterPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response selfRegister(String jsonText) {
        ServiceLogger.LOGGER.info("Received request for session.");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);
        ObjectMapper mapper = new ObjectMapper();
        RegisterPageRequestModel requestModel = null;
        RegisterPageResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, RegisterPageRequestModel.class);
            ServiceLogger.LOGGER.info("Email: " + requestModel.getEmail());
            ServiceLogger.LOGGER.info("Password received.");

            if (requestModel.isValid()) {
                if (requestModel.getPassword().length<7 || requestModel.getPassword().length>16) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(12);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if (!RegisterPageRequestModel.passwordCharacterRequirements(requestModel.getPassword())) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(13);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if (UserRecords.isRegisterEmailAvailable(requestModel)==0) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(16);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if (UserRecords.isRegisterEmailAvailable(requestModel)==1) {
                    if (UserRecords.registerUserToDb(requestModel)) {
                        responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(110);
                        return Response.status(Status.OK).entity(responseModel).build();
                    }
                    else {
                        responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-1);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                }
                else {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-1);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
            else {
                if (requestModel.getPassword() == null) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-12);
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                }
                else if (requestModel.getPassword().length<=0) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-12);
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                }
                else if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-10);
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                }
                else if (!requestModel.getEmail().matches(requestModel.email_regex)) {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-11);
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                }
                else {
                    responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-1);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-3);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-2);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("IOException.");
                responseModel = RegisterPageResponseModel.registerPageResponseModelFactory(-1);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }

    }
}
