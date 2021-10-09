package edu.uci.ics.junyanj1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.junyanj1.service.billing.core.CustomerRecords;
import edu.uci.ics.junyanj1.service.billing.core.OrderRecords;
import edu.uci.ics.junyanj1.service.billing.core.PayPalClient;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

@Path("order")
public class OrderPage {
//    @Path("place")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response placingOrder(@Context HttpHeaders headers,
//                                 String jsonText) {
//        ServiceLogger.LOGGER.info("Received the request to place an order");
//        String email = headers.getHeaderString("email");
//        ServiceLogger.LOGGER.info("email: " + email);
//        String sessionID = headers.getHeaderString("sessionID");
//        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
//
//        ServiceLogger.LOGGER.info(jsonText);
//        OrderRequestModel requestModel;
//        OrderResponseModel responseModel;
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            requestModel = mapper.readValue(jsonText,OrderRequestModel.class);
//            ServiceLogger.LOGGER.info(requestModel.toString());
//
//            Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
//            if (customerCheck == null) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            } else if (customerCheck == false) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(332,null);
//                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//            }
//
//            ArrayList<OrderModel> orders = OrderRecords.CheckOut(requestModel);
//            if (orders == null) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            } else if (orders.isEmpty()) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(341,null);
//                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//            } else {
//                if (OrderRecords.insertOrderToDb(requestModel.getEmail(),orders)) {
//                    responseModel = OrderResponseModel.orderResponseModelFactory(3400,null);
//                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//                } else {
//                    responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            if (e instanceof JsonMappingException) {
//                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-2, null);
//                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
//            } else if (e instanceof JsonParseException) {
//                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-3,null);
//                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
//            } else {
//                ServiceLogger.LOGGER.warning("Other IOException.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            }
//        }
//    }

//    @Path("retrieve")
//    @POST
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response retrieveOrder(@Context HttpHeaders headers,
//                                  String jsonText) {
//        ServiceLogger.LOGGER.info("Received the request to retrieve orders.");
//        String email = headers.getHeaderString("email");
//        String sessionID = headers.getHeaderString("sessionID");
//        ServiceLogger.LOGGER.info("email: " + email);
//        ServiceLogger.LOGGER.info("sessionID: " + sessionID);
//
//        ServiceLogger.LOGGER.info(jsonText);
//        OrderRequestModel requestModel;
//        OrderResponseModel responseModel;
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            requestModel = mapper.readValue(jsonText, OrderRequestModel.class);
//            ServiceLogger.LOGGER.info(requestModel.toString());
//
//            Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
//            if (customerCheck == null) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            } else if (customerCheck == false) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(332,null);
//                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//            }
//
//            ArrayList<OrderModel> orders = OrderRecords.RetrieveOrdersFromDb(requestModel);
//            if (orders == null) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            } else if (orders.isEmpty()) {
//                responseModel = OrderResponseModel.orderResponseModelFactory(3410,null);
//                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//            } else {
////                responseModel = OrderResponseModel.orderResponseModelFactory(3410,orders);
//                responseModel = OrderResponseModel.orderResponseModelFactory(3410,null);
//                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            if (e instanceof JsonMappingException) {
//                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-2, null);
//                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
//            } else if (e instanceof JsonParseException) {
//                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-3,null);
//                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
//            } else {
//                ServiceLogger.LOGGER.warning("Other IOException.");
//                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
//            }
//        }
//    }

    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placingOrder(@Context HttpHeaders headers,
                                 String jsonText) {
        ServiceLogger.LOGGER.info("Received the request to place an order");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        OrderRequestModel requestModel;
        OrderResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText,OrderRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
            if (customerCheck == null) {
                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            } else if (customerCheck == false) {
                responseModel = OrderResponseModel.orderResponseModelFactory(332,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            }

            ArrayList<CheckOutModel> checkouts = OrderRecords.CheckOut(requestModel);
            float o = 0;
            Float orderSum = o;
            for (CheckOutModel c : checkouts) {
                orderSum += c.getUnit_price()*(float)c.getQuantity()*c.getDiscount();
            }

            if (checkouts == null) {
                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            } else if (checkouts.isEmpty()) {
                responseModel = OrderResponseModel.orderResponseModelFactory(341,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                PayPalClient ppc = new PayPalClient();
                DecimalFormat df = new DecimalFormat("0.00");
                String sumStr = df.format(orderSum);
                ServiceLogger.LOGGER.info("The sum is: " + sumStr);
                Map<String,String> map = ppc.createPayment(sumStr);
                if (map.get("status").equals("success")) {
                    URL url = null;
                    String token;
                    try {
                        url = new URL(map.get("redirect_url"));
                        String query = url.getQuery();
                        Map<String,String> queryMap = OrderResponseModel.mapQuery(query);
                        token = queryMap.get("token");

                        if (OrderRecords.insertOrderToDb(token, checkouts)) {
                            responseModel = OrderResponseModel.orderResponseModelFactory(3400,map.get("redirect_url"));
                            return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).build();
                        } else {
                            responseModel = OrderResponseModel.orderResponseModelFactory(-1, null);
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
                        }
                    } catch (Exception e) {
                        ServiceLogger.LOGGER.warning("Failed to parse the payment url: "+map.get("redirect_url"));
                        e.printStackTrace();
                        responseModel = OrderResponseModel.orderResponseModelFactory(-1, null);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionID", sessionID).build();
                    }
                } else {
                    responseModel = OrderResponseModel.orderResponseModelFactory(342,null);
                    return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = OrderResponseModel.orderResponseModelFactory(-2, null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = OrderResponseModel.orderResponseModelFactory(-3,null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }

    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeOrder(@Context HttpHeaders headers,
                                  @QueryParam("paymentId") String paymentId,
                                  @QueryParam("token") String token,
                                  @QueryParam("PayerID") String PayerID) {
        ServiceLogger.LOGGER.info("Received the request to place an order");
        String email = headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("email: " + email);
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info("paymentId: " + paymentId);
        ServiceLogger.LOGGER.info("token: " + token);
        ServiceLogger.LOGGER.info("PayerID: " + PayerID);
        OrderResponseModel responseModel;

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(PayerID);
        try {
            APIContext context = new APIContext(PayPalClient.clientId,PayPalClient.clientSecret,"sandbox");
            Payment createdPayment = payment.execute(context,paymentExecution);
            if (createdPayment != null) {
                Boolean updateResult = OrderRecords.PutTransactionIdInDb(createdPayment,token);
                if (updateResult == null) {
                    responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
                } else if (!updateResult) {
                    responseModel = OrderResponseModel.orderResponseModelFactory(3421,null);
                    return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).build();
                } else {
                    responseModel = OrderResponseModel.orderResponseModelFactory(3420,null);
                    return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).build();
                }
            } else {
                responseModel = OrderResponseModel.orderResponseModelFactory(-1,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Payment can not be completed.");
            responseModel = OrderResponseModel.orderResponseModelFactory(3422,null);
            return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).build();
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrder(@Context HttpHeaders headers,
                                  String jsonText) {
        ServiceLogger.LOGGER.info("Received the request to retrieve orders.");
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("email: " + email);
        ServiceLogger.LOGGER.info("sessionID: " + sessionID);

        ServiceLogger.LOGGER.info(jsonText);
        OrderRequestModel requestModel;
        OrderRetrieveResponseModel responseModel;
        ObjectMapper mapper = new ObjectMapper();

        try {
            requestModel = mapper.readValue(jsonText, OrderRequestModel.class);
            ServiceLogger.LOGGER.info(requestModel.toString());

            Boolean customerCheck = CustomerRecords.isCustomerInDb(requestModel.getEmail());
            if (customerCheck == null) {
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            } else if (customerCheck == false) {
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(332,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            }

            ArrayList<TransactionModel> transactions = OrderRecords.FromEmailToTransactions(requestModel.getEmail());
            if (transactions == null) {
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(-1,null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            } else if (transactions.isEmpty()) {
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(3410,null);
                return Response.status(Status.OK).entity(responseModel).header("email",email).header("sessionID",sessionID).build();
            } else {
                APIContext apiContext = new APIContext(PayPalClient.clientId,PayPalClient.clientSecret,"sandbox");
                Sale sale;
                for (TransactionModel t : transactions) {
                    try {
                        sale = Sale.get(apiContext,t.getTransactionId());
                        t.setState(sale.getState());
                        t.setAmount(new AmountModel(sale.getAmount().getTotal(),sale.getAmount().getCurrency()));
                        t.setTransaction_fee(new TransactionFeeModel(sale.getTransactionFee().getValue(),sale.getTransactionFee().getCurrency()));
                        t.setCreate_time(sale.getCreateTime());
                        t.setUpdate_time(sale.getUpdateTime());

                        ArrayList<ItemModel> items = OrderRecords.RetrieveItemsFromDb(t.getTransactionId());
                        if (items == null) {
                            ServiceLogger.LOGGER.warning("Failed to get items from Db.");
                            continue;
                        } else if (items.isEmpty()) {
                            t.setItems(null);
                        } else {
                            t.setItems(items);
                        }
                    } catch (PayPalRESTException e) {
                        e.printStackTrace();
                        ServiceLogger.LOGGER.warning("Paypal rest exception occurs.");
                        continue;
                    }

                }
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(3410, transactions);
                return Response.status(Status.OK).entity(responseModel).header("email", email).header("sessionID", sessionID).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(-2, null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(-3,null);
                return Response.status(Response.Status.BAD_REQUEST).header("email",email).header("sessionID",sessionID).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.warning("Other IOException.");
                responseModel = OrderRetrieveResponseModel.orderRetrieveResponseModelFactory(-1,null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email",email).header("sessionID",sessionID).build();
            }
        }
    }
}
