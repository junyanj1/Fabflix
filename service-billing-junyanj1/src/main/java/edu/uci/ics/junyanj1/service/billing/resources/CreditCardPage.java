package edu.uci.ics.junyanj1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.billing.core.CreditCardRecords;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardInfoRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardManipulationRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardModel;
import edu.uci.ics.junyanj1.service.billing.models.CreditCardResponseModel;

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
import java.util.Date;

@Path("creditcard")
public class CreditCardPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCreditCards(@Context HttpHeaders headers,
                                        String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert to credit cards table.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CreditCardInfoRequestModel requestModel;
        CreditCardResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CreditCardInfoRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getId().length()<16||requestModel.getId().length()>20){
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getId().matches("[0-9]*")) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(322, null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getExpiration().after(new Date())) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(323,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int insertResult = CreditCardRecords.InsertCCRToDb(requestModel);
                if (insertResult == 325) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(325,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (insertResult == 3200) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(3200,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCard(@Context HttpHeaders headers,
                                     String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update credit cards table.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CreditCardInfoRequestModel requestModel;
        CreditCardResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CreditCardInfoRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getId().length()<16||requestModel.getId().length()>20){
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getId().matches("[0-9]*")) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(322, null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getExpiration().after(new Date())) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(323,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int updateResult = CreditCardRecords.UpdateCCRInDb(requestModel);
                if (updateResult == 324) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(324,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (updateResult == 3210) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(3210,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCard(@Context HttpHeaders headers,
                                     String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete item from credit cards table.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CreditCardManipulationRequestModel requestModel;
        CreditCardResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CreditCardManipulationRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getId().length()<16||requestModel.getId().length()>20){
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getId().matches("[0-9]*")) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(322, null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int deleteResult = CreditCardRecords.DeleteCCRFromDb(requestModel);
                if(deleteResult == 324) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(324,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (deleteResult == 3220) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(3220,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCard(@Context HttpHeaders headers,
                                       String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve item from credit cards table.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CreditCardManipulationRequestModel requestModel;
        CreditCardResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CreditCardManipulationRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getId().length()<16||requestModel.getId().length()>20){
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getId().matches("[0-9]*")) {
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(322, null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                CreditCardModel ccm = CreditCardRecords.RetrieveCCRFromDb(requestModel);
                if (ccm == null) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else if (ccm.getId().equals("nonExist")) {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(324,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CreditCardResponseModel.creditCardResponseModelFactory(3230,ccm);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CreditCardResponseModel.creditCardResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }
}
