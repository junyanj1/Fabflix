package edu.uci.ics.junyanj1.service.api_gateway.resources;

import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.core.GatewayRecords;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.ReportResponseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.ResponsesRequestModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.sql.Connection;

@Path("report")
public class ReportPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response report(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Report is here.");

        String transactionId = headers.getHeaderString("transactionID");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");

        if (transactionId == null) {
            ReportResponseModel responseModel;
            ServiceLogger.LOGGER.warning("TransactionId is not provided.");
            responseModel = ReportResponseModel.reportResponseModelFactory(-18);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        Connection con = GatewayService.getConPool().requestCon();
        ResponsesRequestModel rrm = GatewayRecords.getResponseFromDb(con,transactionId);
        GatewayService.getConPool().releaseCon(con);
        if (rrm == null) {
            ReportResponseModel responseModel = ReportResponseModel.reportResponseModelFactory(-1);
            ResponseBuilder result = Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).header("transactionID",transactionId);
            if (email!=null) {
                result.header("email", email);
            }
            if (sessionId!=null) {
                result.header("sessionID", sessionId);
            }
            return result.build();
        } else if (rrm.getTransactionid().equals("Not found.")) {
            ResponseBuilder result = Response.status(Status.NO_CONTENT).header("delay",GatewayService.getGatewayConfigs().getRequestDelay()).header("transactionID",transactionId);
            if (email!=null) {
                result.header("email", email);
            }
            if (sessionId!=null) {
                result.header("sessionID", sessionId);
            }
            return result.build();
        } else {
            ResponseBuilder result = Response.status(rrm.getHttpstatus()).entity(rrm.getResponse()).header("transactionID",transactionId);
            if (email!=null) {
                result.header("email", email);
            }
            if (rrm.getSessionid()!=null) {
                result.header("sessionID",rrm.getSessionid());
            } else if (sessionId!=null) {
                result.header("sessionID", sessionId);
            }
            con = GatewayService.getConPool().requestCon();
//            GatewayRecords.removeResponseFromDb(con,rrm.getTransactionid());
            GatewayService.getConPool().releaseCon(con);
            return result.build();
        }
    }
}
