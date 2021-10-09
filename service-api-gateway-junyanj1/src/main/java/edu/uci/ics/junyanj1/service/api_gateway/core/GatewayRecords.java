package edu.uci.ics.junyanj1.service.api_gateway.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.junyanj1.service.api_gateway.GatewayService;
import edu.uci.ics.junyanj1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.api_gateway.models.ResponsesRequestModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.IDMSessionVerificationParseModel;
import edu.uci.ics.junyanj1.service.api_gateway.models.idm.IDMSessionVerificationRequestModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GatewayRecords {
    public static boolean storeResultInDb(Connection con, String transactionid, String email, String sessionid, String response, int httpstatus) {
        ServiceLogger.LOGGER.info("Inserting backend results to the database...");

        try {
            String query =
                    "INSERT INTO responses(transactionid,email,sessionid,response,httpstatus)\n" +
                            "VALUES(?,?,?,?,?);";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,transactionid);
            ps.setString(2,email);
            ps.setString(3,sessionid);
            ps.setString(4,response);
            ps.setInt(5,httpstatus);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform insertion.");
            e.printStackTrace();
            return false;
        }
    }

    public static ResponsesRequestModel getResponseFromDb(Connection con, String transactionid) {
        ServiceLogger.LOGGER.info("Retrieving response from database...");

        try {
            String query =
                    "SELECT transactionid,email,sessionid,response,httpstatus\n" +
                            "FROM responses\n" +
                            "WHERE transactionid = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,transactionid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ResponsesRequestModel rrm = new ResponsesRequestModel("Not found.");
            if (rs.next()) {
                rrm.setTransactionid(rs.getString("transactionid"));
                rrm.setEmail(rs.getString("email"));
                rrm.setSessionid(rs.getString("sessionid"));
                rrm.setResponse(rs.getString("response"));
                rrm.setHttpstatus(rs.getInt("httpstatus"));
            }

            return rrm;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform retrieval.");
            e.printStackTrace();
            return null;
        }
    }

    public static Response checkSessionFromIDM(String email, String sessionid) {
        ServiceLogger.LOGGER.info("Verify session with IDM.");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = GatewayService.getIdmConfigs().getIdmUri();
        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();

        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);
        System.out.println(webTarget.toString());

        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        IDMSessionVerificationRequestModel requestModel = new IDMSessionVerificationRequestModel(email,sessionid);
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel,MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        return response;
    }

    public static boolean removeResponseFromDb(Connection con, String transactionid) {
        ServiceLogger.LOGGER.info("Deleting response from database...");

        try {
            String query =
                    "DELETE FROM responses\n" +
                        "WHERE transactionid = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,transactionid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query successful.");

            return true;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform deletion.");
            e.printStackTrace();
            return false;
        }
    }
}
