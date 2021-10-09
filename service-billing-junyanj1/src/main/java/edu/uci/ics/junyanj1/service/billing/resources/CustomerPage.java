package edu.uci.ics.junyanj1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.billing.core.CustomerRecords;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.CustomerEmailRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CustomerInfoRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CustomerModel;
import edu.uci.ics.junyanj1.service.billing.models.CustomerResponseModel;

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

@Path("customer")
public class CustomerPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomer(@Context HttpHeaders headers,
                                   String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert customer.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CustomerInfoRequestModel requestModel;
        CustomerResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CustomerInfoRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getCcId().length()<16||requestModel.getCcId().length()>20) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getCcId().matches("[0-9]*")) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(322,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
                if (customerCheck == null) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else if (customerCheck == true) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(333,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }
                int insertResult = CustomerRecords.InsertCustomerRecordToDb(requestModel);
                if (insertResult == 331) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(331,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (insertResult == 3300) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(3300,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@Context HttpHeaders headers,
                                   String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update customer.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CustomerInfoRequestModel requestModel;
        CustomerResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CustomerInfoRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getCcId().length()<16||requestModel.getCcId().length()>20) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(321,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getCcId().matches("[0-9]*")) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(322,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
                if (customerCheck == null) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else if (customerCheck == false) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(332,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }

                int updateResult = CustomerRecords.UpdateCustomerRecordInDb(requestModel);
                if (updateResult == 331) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(331,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (updateResult == 3310) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(3310,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomer(@Context HttpHeaders headers,
                                     String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve customer info.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        CustomerEmailRequestModel requestModel;
        CustomerResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, CustomerEmailRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
            if (customerCheck == null) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            } else if (customerCheck == false) {
                responseModel = CustomerResponseModel.customerResponseModelFactory(332,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                CustomerModel cm = CustomerRecords.RetrieveCustomerRecordsFromDb(requestModel);
                if (cm == null) {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = CustomerResponseModel.customerResponseModelFactory(3320,cm);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = CustomerResponseModel.customerResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }
}
