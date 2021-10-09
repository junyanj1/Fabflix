package edu.uci.ics.junyanj1.service.api_gateway.threadpool;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.core.GatewayRecords;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.RegisterUserRequestModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.RegisterUserResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id,threadPool);
    }

    public void process() {
        // Do the work
        ClientRequest cr = this.threadPool.remove();
        if (cr == null)
            return;
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);
        WebTarget webTarget = client.target(cr.getURI()).path(cr.getEndpoint());
        if (cr.getQueryParams()!=null) {
            ServiceLogger.LOGGER.info("QueryParams: " + cr.getQueryParams());
            for (String s : cr.getQueryParams().keySet()) {
                webTarget = webTarget.queryParam(s,cr.getQueryParams().getFirst(s));
            }
        }
        ServiceLogger.LOGGER.info("Webtarget: " + webTarget.toString());
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("email",cr.getEmail()).header("sessionID",cr.getSessionID()).header("transactionID",cr.getTransactionID());
        Response response;
        if(cr.getRequest() != null) {
            response = invocationBuilder.method(cr.getRequestMethod(), Entity.entity(cr.getRequest(), MediaType.APPLICATION_JSON));
        } else {
            response = invocationBuilder.method(cr.getRequestMethod());
        }
        // Add response to database
        ObjectMapper mapper = new ObjectMapper();
        if (cr.getEndpoint().equals("/register")) {
            RegisterUserRequestModel requestModel = (RegisterUserRequestModel) cr.getRequest();
            cr.setEmail(requestModel.getEmail());
        }
        Connection rental = GatewayService.getConPool().requestCon();
        if (!GatewayRecords.storeResultInDb(rental,cr.getTransactionID(),cr.getEmail(),cr.getSessionID(),response.readEntity(String.class),response.getStatus())) {
            ServiceLogger.LOGGER.warning("Failed to store the result...");
        }
        GatewayService.getConPool().releaseCon(rental);
    }

    @Override
    public void run() {
        // Get request from queue
        while (true) {
            process();
        }
    }
}
