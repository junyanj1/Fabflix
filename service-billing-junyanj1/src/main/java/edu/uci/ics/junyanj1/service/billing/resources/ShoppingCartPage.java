package edu.uci.ics.junyanj1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.billing.core.ShoppingCartRecords;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.*;

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
import java.util.ArrayList;

@Path("cart")
public class ShoppingCartPage {
    public static String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToShoppingCart(@Context HttpHeaders headers,
                                         String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert to shopping cart.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        ShoppingCartInsertRequestModel requestModel;
        ShoppingCartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, ShoppingCartInsertRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getEmail().matches(email_regex)) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-11,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (requestModel.getQuantity()<=0) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(33,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int insertResult = ShoppingCartRecords.InsertSCRToDb(requestModel);
                if (insertResult == 311) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(311,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (insertResult == 3100) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(3100,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else  {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateShoppingCart(@Context HttpHeaders headers,
                                       String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update the shopping cart.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        ShoppingCartUpdateRequestModel requestModel;
        ShoppingCartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try{
            requestModel = mapper.readValue(jsonText, ShoppingCartUpdateRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getEmail().matches(email_regex)) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-11,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (requestModel.getQuantity()<=0) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(33,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int insertResult = ShoppingCartRecords.UpdateSCRInDb(requestModel);
                if (insertResult == 312) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(312,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (insertResult == 3110) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(3110,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else  {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFromShoppingCart(@Context HttpHeaders headers,
                                           String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete from shopping cart.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        ShoppingCartDeleteRequestModel requestModel;
        ShoppingCartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, ShoppingCartDeleteRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getEmail().matches(email_regex)) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-11,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                int deleteResult = ShoppingCartRecords.DeleteSCRFromDb(requestModel);
                if (deleteResult == 312) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(312,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else if (deleteResult == 3120) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(3120,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else  {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFromShoppingCart(@Context HttpHeaders headers,
                                             String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve records from shopping cart.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        ShoppingCartRetrieveAndClearRequestModel requestModel;
        ShoppingCartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, ShoppingCartRetrieveAndClearRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getEmail().matches(email_regex)) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-11,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                ArrayList<ShoppingCartModel> scm = ShoppingCartRecords.RetrieveSCRFromDb(requestModel);
                if (scm == null) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else if (scm.isEmpty()) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(312,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(3130,scm);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearShoppingCart(@Context HttpHeaders headers,
                                      String jsonText) {
        ServiceLogger.LOGGER.info("Received request to clear records from shopping cart.");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        ShoppingCartRetrieveAndClearRequestModel requestModel;
        ShoppingCartResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, ShoppingCartRetrieveAndClearRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            if (requestModel.getEmail().length()<=0 || requestModel.getEmail().length()>50) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-10,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else if (!requestModel.getEmail().matches(email_regex)) {
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-11,null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                boolean clearResult = ShoppingCartRecords.ClearAllSCRFromDb(requestModel.getEmail());
                if (clearResult) {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(3140,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                } else {
                    responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                    return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-2, null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-3,null);
                return Response.status(Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = ShoppingCartResponseModel.shoppingCartResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }
}
