package edu.uci.ics.junyanj1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.idm.core.UserRecords;
import edu.uci.ics.junyanj1.service.idm.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.idm.models.PrivilegePageRequestModel;
import edu.uci.ics.junyanj1.service.idm.models.PrivilegePageResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("privilege")
public class PrivilegePage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyPrivilegeLevel(String jsonText) {
        ServiceLogger.LOGGER.info("Received privilege level verification request.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        PrivilegePageRequestModel requestModel = null;
        PrivilegePageResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, PrivilegePageRequestModel.class);
            ServiceLogger.LOGGER.info("Email: " + requestModel.getEmail());
            ServiceLogger.LOGGER.info("Plevel: " + requestModel.getPlevel());

            if (requestModel.getPlevel()<1 || requestModel.getPlevel()>5) {
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-14);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-10);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (!requestModel.getEmail().matches(requestModel.email_regex)) {
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-11);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                int userPrivilegeLevel = UserRecords.retrievePrivilegeLevelFromDb(requestModel);
                if (userPrivilegeLevel == -1) {
                    responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-1);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
                else if (userPrivilegeLevel == 0) {
                    responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(14);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if (userPrivilegeLevel > requestModel.getPlevel()) {
                    responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(141);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else if (userPrivilegeLevel <= requestModel.getPlevel()) {
                    responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(140);
                    return Response.status(Status.OK).entity(responseModel).build();
                }
                else {
                    responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-1);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-2);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-3);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = PrivilegePageResponseModel.PrivilegePageResponseModelFactory(-1);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}
