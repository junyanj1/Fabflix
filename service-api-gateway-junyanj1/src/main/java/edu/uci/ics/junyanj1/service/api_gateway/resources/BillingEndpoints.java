package edu.uci.ics.junyanj1.service.api_gateway.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.core.GatewayRecords;
import edu.uci.ics.junyanj1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.billing.*;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.IDMSessionVerificationParseModel;
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

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers,
                                        String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to insert on purchase item.");
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

        ShoppingCartInsertRequestModel requestModel;
        try {
            requestModel = (ShoppingCartInsertRequestModel) ModelValidator.verifyModel(jsonText, ShoppingCartInsertRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartInsertResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
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

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers,
                                      String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to update shopping cart.");
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

        ShoppingCartUpdateRequestModel requestModel;
        try {
            requestModel = (ShoppingCartUpdateRequestModel) ModelValidator.verifyModel(jsonText, ShoppingCartUpdateRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartUpdateResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
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

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers,
                                      String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to delete item from shopping cart.");
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

        ShoppingCartDeleteRequestModel requestModel;
        try {
            requestModel = (ShoppingCartDeleteRequestModel) ModelValidator.verifyModel(jsonText, ShoppingCartDeleteRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartDeleteResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
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

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers,
                                        String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve item from shopping cart.");
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

        ShoppingCartRetrieveRequestModel requestModel;
        try {
            requestModel = (ShoppingCartRetrieveRequestModel) ModelValidator.verifyModel(jsonText, ShoppingCartRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartRetrieveResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
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

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers,
                                     String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to clear the shopping cart.");
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

        ShoppingCartClearRequestModel requestModel;
        try {
            requestModel = (ShoppingCartClearRequestModel) ModelValidator.verifyModel(jsonText, ShoppingCartClearRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, ShoppingCartClearResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
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

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers,
                                            String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to insert a credit card.");
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

        CreditCardInfoRequestModel requestModel;
        try {
            requestModel = (CreditCardInfoRequestModel) ModelValidator.verifyModel(jsonText, CreditCardInfoRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CreditCardInfoResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
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

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers,
                                            String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to insert a credit card.");
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

        CreditCardInfoRequestModel requestModel;
        try {
            requestModel = (CreditCardInfoRequestModel) ModelValidator.verifyModel(jsonText, CreditCardInfoRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CreditCardInfoResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
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

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers,
                                            String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to delete a credit card.");
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

        CreditCardManipulationRequestModel requestModel;
        try {
            requestModel = (CreditCardManipulationRequestModel) ModelValidator.verifyModel(jsonText, CreditCardManipulationRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CreditCardInfoResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
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

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers,
                                              String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve a credit card.");
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

        CreditCardManipulationRequestModel requestModel;
        try {
            requestModel = (CreditCardManipulationRequestModel) ModelValidator.verifyModel(jsonText, CreditCardManipulationRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CreditCardInfoResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
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

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers,
                                          String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to insert customer.");
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

        CustomerInfoRequestModel requestModel;
        try {
            requestModel = (CustomerInfoRequestModel) ModelValidator.verifyModel(jsonText, CustomerInfoRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
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

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers,
                                          String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to update a customer.");
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

        CustomerInfoRequestModel requestModel;
        try {
            requestModel = (CustomerInfoRequestModel) ModelValidator.verifyModel(jsonText, CustomerInfoRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
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

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers,
                                            String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve a customer.");
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

        CustomerEmailRequestModel requestModel;
        try {
            requestModel = (CustomerEmailRequestModel) ModelValidator.verifyModel(jsonText, CustomerEmailRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
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

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers,
                                      String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to place an order.");
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

        OrderRequestModel requestModel;
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
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

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers,
                                         String jsonText) {
        VerifySessionResponseModel verifyResponse;
        ServiceLogger.LOGGER.info("Received request to retrieve an order.");
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

        OrderRequestModel requestModel;
        try {
            requestModel = (OrderRequestModel) ModelValidator.verifyModel(jsonText, OrderRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderResponseModel.class);
        }

        try {
            String transactionID = TransactionIDGenerator.generateTransactionID();
            ClientRequest cr = new ClientRequest();
            cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
            cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
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
