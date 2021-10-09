package edu.uci.ics.junyanj1.service.billing.core;

import edu.uci.ics.junyanj1.service.billing.BillingService;
import edu.uci.ics.junyanj1.service.billing.logger.ServiceLogger;
import edu.uci.ics.junyanj1.service.billing.models.CustomerEmailRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CustomerInfoRequestModel;
import edu.uci.ics.junyanj1.service.billing.models.CustomerModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class CustomerRecords {
    public static int InsertCustomerRecordToDb(CustomerInfoRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to insert customer record to the database...");

        try {
            String query =
                    "INSERT INTO customers(email,firstName,lastName,ccId,address)\n" +
                            "VALUES(?,?,?,?,?);";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getEmail());
            ps.setString(2,requestModel.getFirstName());
            ps.setString(3,requestModel.getLastName());
            ps.setString(4,requestModel.getCcId());
            ps.setString(5,requestModel.getAddress());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            int insertRows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return 3300;
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.warning("ccId Constraint violated.");
                return 331;
            } else {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Unable to perform insertion.");
                return -1;
            }
        }
    }

    public static Boolean isCustomerInDb(String email) {
        ServiceLogger.LOGGER.info("Checking if the customer is already in Database...");

        try {
            String query =
                    "SELECT * FROM customers WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            if (rs.next())
                return true;
            return false;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to perform check.");
            e.printStackTrace();
            return null;
        }
    }

    public static int UpdateCustomerRecordInDb(CustomerInfoRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to update customer record in the database...");

        try {
            String query =
                    "UPDATE customers\n" + "SET firstName = ?, lastName = ?, ccId = ?, address = ?\n" +
                    "WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getFirstName());
            ps.setString(2,requestModel.getLastName());
            ps.setString(3,requestModel.getCcId());
            ps.setString(4,requestModel.getAddress());
            ps.setString(5,requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query successful.");

            return 3310;
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.warning("ccId Constraint violated.");
                return 331;
            } else {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Unable to perform insertion.");
                return -1;
            }
        }
    }

    public static CustomerModel RetrieveCustomerRecordsFromDb(CustomerEmailRequestModel requestModel) {
        ServiceLogger.LOGGER.info("Attempting to retrieve customer records from the database...");

        try {
            String query =
                    "SELECT firstName, lastName, ccId, address\n" +
                            "FROM customers\n" +
                            "WHERE email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1,requestModel.getEmail());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            rs.first();
            return new CustomerModel(
                    requestModel.getEmail(),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("ccId"),
                    rs.getString("address")
            );
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to retrieve customer information.");
            return null;
        }
    }
}
